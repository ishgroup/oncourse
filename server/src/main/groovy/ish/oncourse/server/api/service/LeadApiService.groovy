/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.service

import com.google.inject.Inject
import ish.math.Money
import ish.oncourse.server.api.dao.LeadDao
import ish.oncourse.server.api.v1.function.SaleFunctions
import ish.oncourse.server.api.v1.model.LeadDTO
import ish.oncourse.server.api.v1.model.SaleDTO
import ish.oncourse.server.api.v1.model.SaleTypeDTO
import ish.oncourse.server.api.v1.model.SiteDTO
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.Lead
import ish.oncourse.server.cayenne.LeadAttachmentRelation
import ish.oncourse.server.cayenne.LeadCustomField
import ish.oncourse.server.cayenne.LeadItem
import ish.oncourse.server.cayenne.LeadSite
import ish.oncourse.server.cayenne.LeadTagRelation
import ish.oncourse.server.cayenne.Product
import ish.oncourse.server.cayenne.Site
import ish.oncourse.server.document.DocumentService
import ish.oncourse.server.users.SystemUserService
import ish.util.LocalDateUtils
import org.apache.cayenne.Cayenne
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.Persistent

import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import static ish.oncourse.server.api.v1.function.CustomFieldFunctions.updateCustomFields
import static ish.oncourse.server.api.v1.function.DocumentFunctions.toRestDocument
import static ish.oncourse.server.api.v1.function.DocumentFunctions.updateDocuments
import static ish.oncourse.server.api.v1.function.SaleFunctions.deleteNotActualSellables
import static ish.oncourse.server.api.v1.function.SaleFunctions.toRestSale
import static ish.oncourse.server.api.v1.function.TagFunctions.toRestTagMinimized
import static ish.oncourse.server.api.v1.function.TagFunctions.updateTags

class LeadApiService extends EntityApiService<LeadDTO, Lead, LeadDao> {

    @Inject
    private CourseApiService courseApiService

    @Inject
    private ContactApiService contactApiService

    @Inject
    private DocumentService documentService

    @Inject
    private SystemUserService systemUserService

    @Override
    Class<Lead> getPersistentClass() {
        return Lead.class
    }

    @Override
    LeadDTO toRestModel(Lead cayenneModel) {
        return new LeadDTO().with {dtoModel ->
            dtoModel.id = cayenneModel.id
            dtoModel.createdOn = LocalDateUtils.dateToTimeValue(cayenneModel.createdOn)
            dtoModel.modifiedOn = LocalDateUtils.dateToTimeValue(cayenneModel.modifiedOn)
            dtoModel.studentCount = cayenneModel.studentCount
            dtoModel.contactId = cayenneModel.customer.id
            dtoModel.studentName = cayenneModel.customer.fullName
            dtoModel.studentNotes = cayenneModel.studentNotes
            dtoModel.estimatedValue = cayenneModel.estimatedValue
            dtoModel.nextActionOn = cayenneModel.nextActionOn
            dtoModel.notify = cayenneModel.notify
            dtoModel.active = cayenneModel.status
            dtoModel.assignTo = cayenneModel.assignedTo?.email

            dtoModel.customFields = cayenneModel.customFields.collectEntries {[(it.customFieldType.key): it.value] }
            dtoModel.documents = cayenneModel.activeAttachments.collect { toRestDocument(it.document, it.documentVersion?.id, documentService) }
            dtoModel.tags = cayenneModel.tags.collect { toRestTagMinimized(it) }
            dtoModel.sellables = cayenneModel.items.collect {item -> item.course ? toRestSale(item.course) : toRestSale(item.product) }
            dtoModel
        }
    }

    @Override
    Lead toCayenneModel(LeadDTO dtoModel, Lead cayenneModel) {
        ObjectContext context = cayenneModel.context
        cayenneModel.studentCount = dtoModel.studentCount
        cayenneModel.customer = contactApiService.getEntityAndValidateExistence(context, dtoModel.contactId)
        cayenneModel.estimatedValue = Money.valueOf(dtoModel.estimatedValue)
        cayenneModel.nextActionOn = dtoModel.nextActionOn
        cayenneModel.notify = dtoModel.notify
        cayenneModel.status = dtoModel.active

        if (cayenneModel.newRecord) {
            cayenneModel.assignedTo = context.localObject(systemUserService.currentUser)
        }

        updateLeadItems(dtoModel.sellables, cayenneModel)
        updateSites(dtoModel.sites, cayenneModel)
        updateTags(cayenneModel, cayenneModel.taggingRelations, dtoModel.tags*.id, LeadTagRelation.class, context)
        updateDocuments(cayenneModel, cayenneModel.attachmentRelations, dtoModel.documents, LeadAttachmentRelation.class, context)
        updateCustomFields(context, cayenneModel, dtoModel.customFields, LeadCustomField.class)
        return cayenneModel
    }

    private static void updateLeadItems(List<SaleDTO> saleItemsDto, Lead lead) {
        ObjectContext context = lead.context
        actualizeSellables(context, lead, Course.class,
                lead.items.collect {it.course },
                saleItemsDto.findAll { SaleTypeDTO.COURSE == it.type } as List<SaleDTO>)

        actualizeSellables(context, lead, Product.class,
                lead.items.collect {it.product },
                saleItemsDto.findAll { SaleFunctions.saleTypeMap.values().contains(it.type) } as List<SaleDTO>)
    }

    static void actualizeSellables(ObjectContext context,
                                   Lead lead,
                                   Class<? extends Persistent> dbClass,
                                   List<? extends Persistent> actual,
                                   List<SaleDTO> expected) {

        deleteNotActualSellables(context, actual, expected)

        expected.findAll {saleItem -> !actual.collect {Cayenne.longPKForObject(it) }.contains(saleItem.id) }
                .each {saleItem ->
                    LeadItem leadItem = context.newObject(LeadItem.class)
                    leadItem.lead = lead
                    Persistent object = getRecordById(context, dbClass, saleItem.id)
                    if (Course.class == dbClass) {
                        leadItem.course = object as Course
                    } else if (Product.class == dbClass) {
                        leadItem.product = object as Product
                    }
                }
    }

    private static void updateSites(List<SiteDTO> sites, Lead lead) {
        ObjectContext context = lead.context
        List<Long> sitesToSave = sites*.id ?: [] as List<Long>
        context.deleteObjects(lead.sites.findAll { !sitesToSave.contains(it.site.id) })
        sites.findAll { !lead.sites*.id.contains(it.id) }
                .collect { getRecordById(context, Site, it.id)}
                .each {site ->
                    LeadSite leadSite = context.newObject(LeadSite.class)
                    leadSite.lead = lead
                    leadSite.site = site
                }
    }

    @Override
    void validateModelBeforeSave(LeadDTO dtoModel, ObjectContext context, Long id) {
        if (dtoModel.contactId == null) {
            validator.throwClientErrorException(id, 'contactId', "Need to specify a customer")
        }

        if (!dtoModel.studentCount) {
            validator.throwClientErrorException(id, 'studentCount', "A number of students should be more than zero.")
        }

        if (!dtoModel.active) {
            validator.throwClientErrorException(id, 'active', "Need to specify a lead status.")
        }

        if (dtoModel.sellables.empty) {
            validator.throwClientErrorException(id, 'active', "A lead should consist of at least one sale item.")
        }
    }

    @Override
    void validateModelBeforeRemove(Lead cayenneModel) {

    }

    @Override
    Closure getAction (String key, String value) {
        Closure action = super.getAction(key, value)
        if (!action) {
            validator.throwClientErrorException(key, "Unsupported attribute")
        }
        action
    }
}
