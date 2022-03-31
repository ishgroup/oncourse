/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.service.impl

import com.google.inject.Inject
import ish.common.types.ApplicationStatus
import ish.common.types.LeadStatus
import ish.math.Money
import ish.oncourse.server.ICayenneService
import ish.oncourse.server.api.dao.ContactDao
import ish.oncourse.server.api.v1.model.ContactInsightDTO
import ish.oncourse.server.api.v1.model.ContactInteractionDTO
import ish.oncourse.server.api.v1.model.ContactOverviewDTO
import ish.oncourse.server.api.v1.service.ContactInsightApi
import ish.oncourse.server.cayenne.AssessmentSubmission
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.InvoiceLine
import ish.oncourse.server.document.DocumentService

import static ish.oncourse.server.api.v1.function.DocumentFunctions.getProfilePicture

class ContactInsightApiImpl implements ContactInsightApi{
    @Inject
    private ContactDao contactDao

    @Inject
    private ICayenneService cayenneService

    @Inject
    private DocumentService documentService

    @Override
    ContactInsightDTO getInsight(Long id) {
        def context = cayenneService.newReadonlyContext
        def contact = contactDao.getById(context, id)


        def contactOverview = new ContactOverviewDTO()
        contactOverview.firstSeen = contact.createdOn.toLocalDate()
        contactOverview.owing = (contact.student?.invoices*.amountOwing.sum() as Money ?: Money.ZERO).toBigDecimal()
        contactOverview.spent = (contact.student?.invoices*.amountPaid.sum() as Money ?: Money.ZERO).toBigDecimal()
        contactOverview.enrolments(contact.student?.enrolments?.collect {it.id})
        contactOverview.openApplications(contact?.student?.applications?.findAll {it.status != ApplicationStatus.REJECTED && it.status != ApplicationStatus.WITHDRAWN}?.collect {it.id})
        contactOverview.closeApplications(contact?.student?.applications?.findAll {it.status == ApplicationStatus.REJECTED || it.status == ApplicationStatus.WITHDRAWN}?.collect {it.id})
        contactOverview.openLeads(contact.leads.findAll{it.status == LeadStatus.OPEN}.collect{it.id})
        contactOverview.closeLeads(contact.leads.findAll{it.status == LeadStatus.CLOSED}.collect{it.id})


        def contactInsight = new ContactInsightDTO()
        contactInsight.fullName = contact?.fullName
        contactInsight.email = contact?.email
        contactInsight.fax = contact?.fax
        contactInsight.homePhone = contact?.homePhone
        contactInsight.workPhone = contact?.workPhone
        contactInsight.mobilePhone = contact?.mobilePhone
        contactInsight.profilePicture = getProfilePicture(contact, documentService)

        contactInsight.interactions = interactionsOf(contact)
        contactInsight.overview(contactOverview)
        contactInsight
    }

    private static List<ContactInteractionDTO> interactionsOf(Contact contact){
        def interactions = new ArrayList<ContactInteractionDTO>()
        interactions.addAll(contact?.student?.enrolments?.collect {it.toInteraction()})
        interactions.addAll(contact?.student?.applications?.collect {it.toInteraction()})
        interactions.addAll(contact?.student?.waitingLists?.collect{it.toInteraction()})
        interactions.addAll(contact?.leads?.collect{it.toInteraction()})
        interactions.addAll(contact?.paymentsIn?.collect{it.toInteraction()})
        interactions.addAll(contact?.paymentsOut?.collect{it.toInteraction()})
        interactions.addAll(contact?.invoices?.collect{it.toInteraction()})
        interactions.addAll(contact?.quotes?.collect{it.toInteraction()})
        interactions.addAll((contact?.student?.enrolments*.assessmentSubmissions?.flatten() as List<AssessmentSubmission>)?.collect{it.toInteraction()})
        interactions.addAll(contact?.student?.certificates?.collect{it.toInteraction()})
        interactions.addAll(contact?.payslips?.collect{it.toInteraction()})
        interactions.addAll(contact?.productItems?.collect{it.toInteraction()})
        interactions.addAll(contact?.noteRelations*.note.collect{it.toInteraction()})
        interactions.addAll()
        interactions
    }
}
