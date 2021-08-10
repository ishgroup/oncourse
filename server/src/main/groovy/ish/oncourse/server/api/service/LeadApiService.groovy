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
import ish.oncourse.server.api.v1.model.LeadStatusDTO
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
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.document.DocumentService
import ish.oncourse.server.users.SystemUserService
import ish.util.LocalDateUtils
import org.apache.cayenne.ObjectContext

import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import static ish.oncourse.server.api.v1.function.CustomFieldFunctions.updateCustomFields
import static ish.oncourse.server.api.v1.function.DocumentFunctions.toRestDocument
import static ish.oncourse.server.api.v1.function.DocumentFunctions.updateDocuments
import static ish.oncourse.server.api.v1.function.SaleFunctions.toRestSale
import static ish.oncourse.server.api.v1.function.SiteFunctions.toRestSiteMinimized
import static ish.oncourse.server.api.v1.function.TagFunctions.toRestTagMinimized
import static ish.oncourse.server.api.v1.function.TagFunctions.updateTags

class LeadApiService extends TaggableApiService<LeadDTO, Lead, LeadDao> {

    @Inject
    private CourseApiService courseApiService

    @Inject
    private ContactApiService contactApiService

    @Inject
    private DocumentService documentService

    @Inject
    private InvoiceApiService invoiceApiService

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
            dtoModel.contactName = cayenneModel.customer.fullName
            dtoModel.studentNotes = cayenneModel.studentNotes
            dtoModel.estimatedValue = cayenneModel.estimatedValue.toBigDecimal()
            dtoModel.nextActionOn = LocalDateUtils.dateToTimeValue(cayenneModel.nextActionOn)
            dtoModel.status = LeadStatusDTO.values()[0].fromDbType(cayenneModel.status)
            dtoModel.assignToId = cayenneModel.assignedTo?.id
            dtoModel.assignTo = cayenneModel.assignedTo?.fullName

            dtoModel.invoices = cayenneModel.invoices.collect {invoiceApiService.toRestLeadInvoice(it) } +
                    cayenneModel.quotes.collect {invoiceApiService.toRestLeadInvoice(it) }
            dtoModel.customFields = cayenneModel.customFields.collectEntries {[(it.customFieldType.key): it.value] }
            dtoModel.documents = cayenneModel.activeAttachments.collect { toRestDocument(it.document, it.documentVersion?.id, documentService) }
            dtoModel.tags = cayenneModel.tags.collect { toRestTagMinimized(it) }
            dtoModel.relatedSellables = cayenneModel.items.collect {item -> item.course ? toRestSale(item.course) : toRestSale(item.product) }
            dtoModel.sites =  cayenneModel.sites.collect {toRestSiteMinimized(it) }
            dtoModel
        }
    }

    @Override
    Lead toCayenneModel(LeadDTO dtoModel, Lead cayenneModel) {
        ObjectContext context = cayenneModel.context
        cayenneModel.studentCount = dtoModel.studentCount
        cayenneModel.customer = contactApiService.getEntityAndValidateExistence(context, dtoModel.contactId)
        cayenneModel.estimatedValue = dtoModel.estimatedValue != null ? Money.valueOf(dtoModel.estimatedValue) : null as Money
        cayenneModel.nextActionOn = LocalDateUtils.timeValueToDate(dtoModel.nextActionOn)
        cayenneModel.status = dtoModel.status.getDbType()

        if (dtoModel.assignToId ==  null) {
            cayenneModel.assignedTo = context.localObject(systemUserService.currentUser)
        } else {
            cayenneModel.assignedTo = getRecordById(context, SystemUser.class, dtoModel.assignToId)
        }

        updateLeadItems(cayenneModel, dtoModel.relatedSellables)
        updateSites(dtoModel.sites, cayenneModel)
        updateTags(cayenneModel, cayenneModel.taggingRelations, dtoModel.tags*.id, LeadTagRelation.class, context)
        updateDocuments(cayenneModel, cayenneModel.attachmentRelations, dtoModel.documents, LeadAttachmentRelation.class, context)
        updateCustomFields(context, cayenneModel, dtoModel.customFields, LeadCustomField.class)
        return cayenneModel
    }

    private void updateLeadItems(Lead lead, List<SaleDTO> saleItemsDto) {
        actualizeCourses(lead, saleItemsDto.findAll { SaleTypeDTO.COURSE == it.type } as List<SaleDTO>)
        actualizeProducts(lead, saleItemsDto.findAll { SaleFunctions.saleTypeMap.values().contains(it.type) } as List<SaleDTO>)
    }

    private void actualizeCourses(Lead lead, List<SaleDTO> expectedCourses) {
        ObjectContext context = lead.context
        context.deleteObjects(lead.courses.findAll {!expectedCourses*.id.contains(it.id) })

        expectedCourses.findAll {!lead.courses*.id.contains(it.id) }
                .each {saleItem ->
                    LeadItem leadItem = context.newObject(LeadItem.class)
                    leadItem.lead = lead
                    leadItem.course = courseApiService.getEntityAndValidateExistence(context, saleItem.id)
                }
    }

    private void actualizeProducts(Lead lead, List<SaleDTO> expectedProducts) {
        ObjectContext context = lead.context
        context.deleteObjects(lead.products.findAll {!expectedProducts*.id.contains(it.id) })

        expectedProducts.findAll {!lead.products*.id.contains(it.id) }
                .each {saleItem ->
                    LeadItem leadItem = context.newObject(LeadItem.class)
                    leadItem.lead = lead
                    leadItem.product = getRecordById(context, Product.class, saleItem.id) as Product
                }
    }

    private static void updateSites(List<SiteDTO> sites, Lead lead) {
        ObjectContext context = lead.context
        List<Long> sitesToSave = sites*.id ?: [] as List<Long>
        context.deleteObjects(lead.sites.findAll { !sitesToSave.contains(it.id) })
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

        if (dtoModel.status == null) {
            validator.throwClientErrorException(id, 'status', "Need to specify a lead status.")
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

    private Money calculateEstimatedValue(ObjectContext context, Integer studentCounts, List<SaleDTO> relatedCourses) {
        if (!relatedCourses.empty) {
            List<Course> dbCourses = relatedCourses.collect {courseApiService.getEntityAndValidateExistence(context, it.id) }
            Money estimatedValue = dbCourses.findAll {!it.courseClasses.empty }
                    .collect {dbCourse ->
                        // take a last class of course
                        return dbCourse.courseClasses.sort { it.startDateTime }.last()
                    }*.feeExGst.sum() as Money

            return estimatedValue.multiply(studentCounts.toBigDecimal())
        }
        return Money.ZERO
    }
}
