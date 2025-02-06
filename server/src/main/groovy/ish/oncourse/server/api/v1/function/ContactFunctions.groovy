/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.api.v1.function

import ish.common.types.AttachmentSpecialType
import ish.common.types.PaymentSource
import ish.math.Money
import ish.oncourse.cayenne.FinancialItem
import ish.oncourse.server.api.dao.ContactDao
import ish.oncourse.server.api.dao.ContactRelationDao
import ish.oncourse.server.api.dao.ContactRelationTypeDao
import ish.oncourse.server.api.v1.model.CartDTO
import ish.oncourse.server.api.v1.model.ContactDTO
import ish.oncourse.server.api.v1.model.MessagePersonDTO
import ish.oncourse.server.api.v1.model.MessageStatusDTO
import ish.oncourse.server.api.v1.model.MessageTypeDTO
import ish.oncourse.server.api.v1.model.StudentConcessionDTO
import ish.oncourse.server.api.v1.model.StudentDTO
import ish.oncourse.server.cayenne.ConcessionType
import ish.oncourse.server.cayenne.Message
import ish.oncourse.server.api.v1.model.ContactRelationDTO
import ish.oncourse.server.api.v1.model.DocumentDTO
import ish.oncourse.server.api.v1.model.PaymentStatusDTO
import ish.oncourse.server.api.validation.EntityValidator
import ish.oncourse.server.cayenne.ContactRelationType
import ish.oncourse.server.cayenne.Invoice
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentOut
import ish.oncourse.server.api.v1.model.FinancialLineDTO
import ish.oncourse.server.api.v1.model.FinancialTypeDTO
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.ContactAttachmentRelation
import ish.oncourse.server.cayenne.ContactRelation
import ish.oncourse.server.cayenne.Document
import ish.oncourse.server.cayenne.InvoiceLine
import ish.util.InvoiceUtil
import ish.util.LocalDateUtils
import ish.util.UsiUtil
import ish.validation.ValidationUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.Persistent
import org.apache.cayenne.exp.Property
import org.apache.cayenne.query.ObjectSelect
import org.apache.cayenne.query.Ordering
import org.apache.cayenne.query.PrefetchTreeNode
import org.apache.cayenne.query.SortOrder
import org.apache.commons.lang3.StringUtils
import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import static org.apache.commons.lang3.StringUtils.isBlank

import java.time.LocalDate

class ContactFunctions {

    static void mergeToContactRelationshipsToA(Contact a, Contact b) {
        if (b.toContacts.size() > 0) {
            List<ContactRelation> con2ToContacts = new ArrayList<>(b.toContacts)

            for (ContactRelation cr2 : con2ToContacts) {
                if (cr2.toContact == a) {
                    b.context.deleteObjects(cr2)
                }
                for (ContactRelation cr1 : a.toContacts) {
                    if (cr1.toContact == cr2.toContact && cr1.relationType == cr2.relationType) {
                        b.context.deleteObjects(cr2)
                    }
                }
            }
        }
    }

    static void mergeFromContactRelationshipsToA(Contact a, Contact b) {
        if (b.fromContacts.size() > 0) {
            List<ContactRelation> con2FromContacts = new ArrayList<>(b.fromContacts)
            for (ContactRelation cr2 : con2FromContacts) {
                if (cr2.fromContact == a) {
                    b.context.deleteObjects(cr2)
                }
                for (ContactRelation cr1 : a.fromContacts) {
                    if (cr1.fromContact == cr2.fromContact && cr1.relationType == cr2.relationType) {
                        b.context.deleteObjects(cr2)
                    }
                }
            }
        }
    }

    static void mergeContactAttachmentRelationsToA(ObjectContext context, Contact a, Contact b) {
        // merging attachment relationships requires special code to avoid creating duplicates:
        // do not move attachment relations to attachments which both contacts already linked to
        if (b.attachmentRelations.size() > 0) {
            List<ContactAttachmentRelation> con2AttachmentRelations = new ArrayList<>(b.attachmentRelations)
            for (ContactAttachmentRelation con2relation : con2AttachmentRelations) {
                for (ContactAttachmentRelation con1relation : a.attachmentRelations) {
                    if (con1relation.document == con2relation.document) {
                        context.deleteObjects(con2relation)
                    }
                }
            }
        }
    }

    static void mergeContactToManyRelationshipsToA(Contact a, Contact b, List<String> mergeableProperties) {
        //merge the Contact relationships
        for (String property : mergeableProperties) {
            if (b.getValueForKey(property) != null && b.getValueForKey(property) instanceof List) {
                EntityFunctions.moveToManyEntityRelationshipToA(b, a, property)
            }
        }
    }

    static Document getProfilePictureDocument(Contact contact) {
        for (ContactAttachmentRelation relation : contact.attachmentRelations) {
            if (AttachmentSpecialType.PROFILE_PICTURE == relation.specialType) {
                return relation.document
            }
        }
        null
    }

    static boolean isValidUsiString(String usi) {
        StringUtils.trimToNull(usi) != null && usi.length() == 10 && UsiUtil.validateKey(usi)
    }

    static boolean isValidEmailAddress(String email) {
        !StringUtils.isBlank(email) && !ValidationUtil.isValidEmailAddress(email)
    }

    static void updateProfilePicture(Contact contact, DocumentDTO pictureDocument) {
        ContactAttachmentRelation existedPictureRel = contact.attachmentRelations.find { rel -> AttachmentSpecialType.PROFILE_PICTURE == rel.specialType }
        if (existedPictureRel?.document?.id != pictureDocument?.id) {
            if (existedPictureRel == null) {
                existedPictureRel = contact.context.newObject(ContactAttachmentRelation)
                existedPictureRel.attachedContact = contact
                existedPictureRel.specialType = AttachmentSpecialType.PROFILE_PICTURE
            }
            if (pictureDocument != null) {
                existedPictureRel.document = getRecordById(contact.context, Document, pictureDocument.id)
            } else {
                contact.context.deleteObject(existedPictureRel)
            }
        }
    }

    static List<FinancialLineDTO> getFinancialDataForContact(Contact contact) {
        List<FinancialLineDTO> lines = new ArrayList<>()

        lines.addAll(getContactRelatedData(contact, Invoice.class, Invoice.CONTACT, Invoice.INVOICE_DATE, SortOrder.ASCENDING,  Invoice.INVOICE_LINES.joint(), Invoice.INVOICE_LINES.dot(InvoiceLine.TAX).joint())
                .collect { i ->
                    new FinancialLineDTO().with {
                        l ->
                            l.relatedEntityId = i.id
                            l.type = FinancialTypeDTO.INVOICE
                            l.description = "${InvoiceUtil.sumInvoiceLines(i.invoiceLines).isLessThan(Money.ZERO()) ? 'Credit note' : 'Invoice'} (${i.source.displayName})"
                            l.date = i.invoiceDate
                            l.createdOn = LocalDateUtils.dateToTimeValue(i.createdOn)
                            l.referenceNumber = i.invoiceNumber
                            l.status = PaymentStatusDTO.SUCCESS
                            l.owing = i.amountOwing.toBigDecimal()
                            l.amount = i.totalIncTax.toBigDecimal()
                            l.balance = null
                            l
                    }
                })
        lines.addAll(getContactRelatedData(contact, PaymentIn.class, PaymentIn.PAYER, PaymentIn.PAYMENT_DATE, SortOrder.ASCENDING, PaymentIn.PAYMENT_METHOD.joint())
                .collect { pi ->
                    new FinancialLineDTO().with {
                        l ->
                            l.relatedEntityId = pi.id
                            l.type = FinancialTypeDTO.PAYMENTIN
                            l.description = "${pi.paymentMethod.name} payment ${pi.typeOfPayment} (${pi.source.displayName})".toString()
                            l.date = pi.paymentDate
                            l.createdOn = LocalDateUtils.dateToTimeValue(pi.createdOn)
                            l.referenceNumber = pi.gatewayReference
                            l.status = PaymentStatusDTO.values()[0].fromDbType(pi.status)
                            l.owing = null
                            l.amount = pi.amount.toBigDecimal()
                            l.balance = null
                            l
                    }
                })
        lines.addAll(getContactRelatedData(contact, PaymentOut.class, PaymentOut.PAYEE, PaymentOut.PAYMENT_DATE, SortOrder.ASCENDING,  PaymentOut.PAYMENT_METHOD.joint())
                .collect { po ->
                    new FinancialLineDTO().with {
                        l ->
                            l.relatedEntityId = po.id
                            l.type = FinancialTypeDTO.PAYMENTOUT
                            l.description = "${po.paymentMethod.name} payment ${po.typeOfPayment} (${PaymentSource.SOURCE_ONCOURSE.displayName})"
                            l.date = po.paymentDate
                            l.createdOn = LocalDateUtils.dateToTimeValue(po.createdOn)
                            l.referenceNumber = po.gatewayReference
                            l.status = PaymentStatusDTO.values()[0].fromDbType(po.status)
                            l.owing = null
                            l.amount = po.amount.toBigDecimal()
                            l.balance = null
                            l
                    }
                })

        BigDecimal total = new BigDecimal(0)

        Ordering.orderList(lines
                , Arrays.asList(new Ordering(FinancialItem.DATE, SortOrder.ASCENDING), new Ordering(FinancialItem.CREATED_ON, SortOrder.ASCENDING)))

        lines.each { i ->
            if (FinancialTypeDTO.INVOICE == i.type) {
                total = total.add(i.getAmount());
            } else if (![PaymentStatusDTO.FAILED,
                         PaymentStatusDTO.CARD_DECLINED,
                         PaymentStatusDTO.REJECTED_NO_PLACES_AVAILABLE,
                         PaymentStatusDTO.NOT_PROCESSED].contains(i.status)) {
                if (FinancialTypeDTO.PAYMENTIN == i.type) {
                    total = total.subtract(i.amount);
                } else {
                    total = total.add(i.amount);
                }
            }
            i.balance = total
        }

        lines
    }

    private static <T extends Persistent> List<T> getContactRelatedData(Contact contact, Class<T> clazz, Property<Contact> contactProperty, Property<LocalDate> orderProperty, SortOrder order, PrefetchTreeNode... prefetch) {
        ObjectSelect<T> objectSelect = ObjectSelect.query(clazz)
                .where(contactProperty.eq(contact))

        for (PrefetchTreeNode treeNode : prefetch) {
            objectSelect.prefetch(treeNode)
        }

        return objectSelect.select(contact.context)
    }

    static void updateContactRelations(ObjectContext context, Contact contact, List<ContactRelationDTO> relations) {
        List<ContactRelation> toDelete = []

        toDelete += contact.fromContacts.findAll{!(it.id in relations*.id)}
        toDelete += contact.toContacts.findAll{!(it.id in relations*.id)}
        context.deleteObjects(toDelete)


        List<ContactRelation> dbRelations = []
        dbRelations += contact.fromContacts
        dbRelations += contact.toContacts

        relations.each { dto ->
            ContactRelation rel = dbRelations.find { it.id == dto.id }
            if (rel == null) {
                rel = context.newObject(ContactRelation)
            }
            rel.fromContact = dto.contactFromId ? getRecordById(context, Contact, dto.contactFromId) : contact
            rel.toContact = dto.contactToId ? getRecordById(context, Contact, dto.contactToId) : contact
            rel.relationType = getRecordById(context, ContactRelationType, dto.relationId)
        }

    }

    static MessagePersonDTO toRestMessagePerson(Message message) {
        new MessagePersonDTO().with { m ->
            m.messageId = message.id
            m.createdOn = LocalDateUtils.dateToTimeValue(message.createdOn)
            m.sentOn = LocalDateUtils.dateToTimeValue(message.timeOfDelivery)
            m.creatorKey = message.creatorKey
            m.subject = message.emailSubject
            m.status = MessageStatusDTO.values()[0].fromDbType(message.status)
            m.type = MessageTypeDTO.values()[0].fromDbType(message.type)
            m
        }
    }

    static void updateAbandonedCarts(ObjectContext context, Contact contact, List<CartDTO> carts){
        def removedCarts = contact.abandonedCarts.findAll { !(it.id in carts*.id) }
        context.deleteObjects(removedCarts)
        context.commitChanges()
    }

    static void validateContactRelations(ObjectContext context, ContactDao contactDao, ContactRelationTypeDao contactRelationTypeDao, ContactRelationDao contactRelationDao, EntityValidator validator, ContactDTO contact) {
        if (contact.relations != null) {

            if (contact.relations.find{it.relationId == null}) {
                validator.throwClientErrorException(contact?.id, 'relations', "Contact relation type shouldn't be empty.")
            }

            List<String> duplicatesSearchInput = contact.relations.collect{"${it.contactFromId != null ? it.contactFromId : contact?.id}-${it.contactToId != null ? it.contactToId : contact?.id}-${it.relationId}".toString()}
            List<String> uniques = []
            duplicatesSearchInput.each {
                if (uniques.contains(it)) {
                    validator.throwClientErrorException(contact?.id, 'relations', "Contact relations can not contain duplicate.")
                } else {
                    uniques += it
                }
            }


            contact.relations.each { rel ->
                if (rel.contactFromId == null && rel.contactToId == null) {
                    validator.throwClientErrorException(contact?.id, 'relations', "Both contacts in contact relation are empty.")
                }

                if ((rel.contactFromId == null && rel.contactToId == contact?.id) || (rel.contactFromId == contact?.id && rel.contactToId == null)) {
                    validator.throwClientErrorException(contact?.id, 'relations', "Contact can not be related to self.")
                }

                if ((rel.contactFromId == contact?.id && rel.contactToId == contact?.id) || (rel.contactFromId != null && rel.contactToId != null)) {
                    validator.throwClientErrorException(contact?.id, 'relations', "One of contact should be null (current), second contact should point to other contact.")
                }

                if (contactRelationTypeDao.getById(context, rel.relationId) == null) {
                    validator.throwClientErrorException(contact?.id, 'relations', "Contact relation type does not exist.")
                }
                if (rel.contactFromId != null && contactDao.getById(context, rel.contactFromId) == null) {
                    validator.throwClientErrorException(contact?.id, 'relations', "Contact 'from' does not exist.")
                }
                if (rel.contactToId != null && contactDao.getById(context, rel.contactToId) == null) {
                    validator.throwClientErrorException(contact?.id, 'relations', "Contact 'to' does not exist.")
                }
            }
        }
    }

    static void validateStudentConcessions(ObjectContext context, EntityValidator validator, StudentDTO student) {

        if (student.concessions) {
            student.concessions.eachWithIndex { StudentConcessionDTO c, int i ->
                ConcessionType type = getRecordById(context, ConcessionType, c?.type?.id)

                if (type == null) {
                    validator.throwClientErrorException("student.concessions[$i].type.id", "You need to enter a concession type.")
                }

                if (type?.hasConcessionNumber && isBlank(c.number)) {
                    validator.throwClientErrorException("student.concessions[$i].number", "You need to enter a concession number.")
                }

                if (type?.hasExpiryDate && c.expiresOn == null) {
                    validator.throwClientErrorException("student.concessions[$i].expiresOn", "You need to enter a expiry date.")
                }
            }

        }
    }

    static void validateStudentYearSchoolCompleted(EntityValidator validator, StudentDTO student) {
        if (student.yearSchoolCompleted != null) {

            final int givenYear = student.yearSchoolCompleted
            if (givenYear > Calendar.getInstance().get(Calendar.YEAR)) {
                validator.throwClientErrorException("student.yearSchollCompleted", 'Year school completed cannot be in the future if supplied.')
            } else if (givenYear < 1940) {
                validator.throwClientErrorException("student.yearSchollCompleted", "Year school completed if supplied should be within not earlier than ${givenYear}.".toString())
            }
        }
    }
}
