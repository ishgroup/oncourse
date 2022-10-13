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
import ish.oncourse.server.cayenne.*
import ish.oncourse.server.document.DocumentService
import ish.util.LocalDateUtils
import org.apache.cayenne.exp.Expression
import org.apache.cayenne.exp.Property
import org.apache.cayenne.exp.parser.ASTFalse
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.PrefetchTreeNode

import static ish.oncourse.server.api.v1.function.DocumentFunctions.getProfilePicture

class ContactInsightApiImpl implements ContactInsightApi {
    @Inject
    private ContactDao contactDao

    @Inject
    private ICayenneService cayenneService

    @Inject
    private DocumentService documentService

    private static Map<String, List<Long>> entitiesWithAttachments = new HashMap<>()

    @Override
    ContactInsightDTO getInsight(Long id) {
        def context = cayenneService.newReadonlyContext
        def contact = contactDao.getById(context, id)
        def student = contact.student
        def invoices = contact.invoices
        def applications = student?.applications
        def enrolments = student?.enrolments

        def contactOverview = new ContactOverviewDTO()
        contactOverview.firstSeen = LocalDateUtils.dateToTimeValue(contact.createdOn)
        contactOverview.owing = contact.totalOwing.toBigDecimal()
        contactOverview.spent = contact.totalInvoiced.toBigDecimal()
        contactOverview.enrolments(enrolments?.collect { it.id } ?: new ArrayList<Long>())
        contactOverview.openApplications(applications
                ?.findAll { it.status != ApplicationStatus.REJECTED && it.status != ApplicationStatus.WITHDRAWN }
                ?.collect { it.id } ?: new ArrayList<Long>())
        contactOverview.closeApplications(applications
                ?.findAll { it.status == ApplicationStatus.REJECTED || it.status == ApplicationStatus.WITHDRAWN }
                ?.collect { it.id } ?: new ArrayList<Long>())
        contactOverview.openLeads(contact.leads.findAll { it.status == LeadStatus.OPEN }.collect { it.id })
        contactOverview.closeLeads(contact.leads.findAll { it.status == LeadStatus.CLOSED }.collect { it.id })

        entitiesWithAttachments.put(Invoice.class.simpleName, invoices?.collect { it.id })
        entitiesWithAttachments.put(Enrolment.class.simpleName, enrolments?.collect { it.id })
        entitiesWithAttachments.put(Application.class.simpleName, applications?.collect { it.id })
        entitiesWithAttachments.put(Lead.class.simpleName, contact.leads.collect { it.id })


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

    private List<ContactInteractionDTO> interactionsOf(Contact contact) {
        def student = contact?.student
        def enrolments = student?.enrolments
        def certificates = student?.certificates
        def payslips = contact?.payslips
        def productItems = contact?.productItems
        def assessmentSubmissions = enrolments*.assessmentSubmissions?.flatten() as List<AssessmentSubmission>
        def interactions = new ArrayList<ContactInteractionDTO>()
        interactions.addAll(interactionsOfList(enrolments))
        interactions.addAll(interactionsOfList(student?.applications))
        interactions.addAll(interactionsOfList(student?.waitingLists))
        interactions.addAll(interactionsOfList(contact?.leads))
        interactions.addAll(interactionsOfList(contact?.paymentsIn))
        interactions.addAll(interactionsOfList(contact?.paymentsOut))
        interactions.addAll(interactionsOfList(contact?.invoices))
        interactions.addAll(interactionsOfList(contact?.quotes))
        interactions.addAll(interactionsOfList(assessmentSubmissions))
        interactions.addAll(interactionsOfList(certificates))
        interactions.addAll(interactionsOfList(payslips))
        interactions.addAll(interactionsOfList(productItems))

        entitiesWithAttachments.put(Contact.class.simpleName, List.of(contact?.id))
        entitiesWithAttachments.put(PaymentIn.class.simpleName, contact?.paymentsIn?.collect { it.id })
        entitiesWithAttachments.put(PaymentOut.class.simpleName, contact?.paymentsOut?.collect { it.id })
        entitiesWithAttachments.put(Quote.class.simpleName, contact?.quotes?.collect { it.id })
        entitiesWithAttachments.put(AssessmentSubmission.class.simpleName, assessmentSubmissions?.collect { it.id })
        entitiesWithAttachments.put(Certificate.class.simpleName, certificates?.collect { it.id })
        entitiesWithAttachments.put(Payslip.class.simpleName, payslips?.collect { it.id })
        entitiesWithAttachments.put(ProductItem.class.simpleName, productItems?.collect { it.id })


        def notes = ObjectSelect.query(Note)
                .where(buildNotesExpression())
                .prefetch(Note.SYSTEM_USER.path().toString(), PrefetchTreeNode.DISJOINT_BY_ID_PREFETCH_SEMANTICS)
                .select(cayenneService.newReadonlyContext)
        def attachmentRelations = ObjectSelect.query(AttachmentRelation)
                .where(buildDocumentsExpression())
                .prefetch(AttachmentRelation.DOCUMENT.path().toString(), PrefetchTreeNode.DISJOINT_BY_ID_PREFETCH_SEMANTICS)
                .select(cayenneService.newReadonlyContext)


        interactions.addAll(interactionsOfList(notes))
        interactions.addAll(interactionsOfList(attachmentRelations))
        interactions.sort { a, b -> b.date <=> a.date }

        interactions
    }

    private static Expression buildNotesExpression() {
        buildRelationExpression(Note.NOTE_RELATIONS.dot(NoteRelation.ENTITY_IDENTIFIER),Note.NOTE_RELATIONS.dot(NoteRelation.ENTITY_RECORD_ID))
    }

    private static Expression buildDocumentsExpression() {
        buildRelationExpression(AttachmentRelation.ENTITY_IDENTIFIER,AttachmentRelation.ENTITY_RECORD_ID)
    }

    private static Expression buildRelationExpression(Property<String>entityIdentifierProperty, Property<Long> entityIdProperty){
        Expression expression = new ASTFalse()
        for (def entry : entitiesWithAttachments.entrySet()) {
            if(entry.getValue() && !entry.getValue().isEmpty()) {
                expression = expression.orExp(
                        entityIdentifierProperty.eq(entry.getKey())
                                .andExp(
                                        entityIdProperty.in(entry.getValue())
                                )
                )
            }
        }
        expression
    }

    private static List<ContactInteractionDTO> interactionsOfList(List<? extends ContactActivityTrait> records) {
        records?.collect { it.toInteraction() } ?: new ArrayList<ContactInteractionDTO>()
    }
}
