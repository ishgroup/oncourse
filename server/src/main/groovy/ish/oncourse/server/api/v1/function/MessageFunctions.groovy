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

import groovy.transform.CompileDynamic
import ish.common.types.EnrolmentStatus
import ish.common.types.MessageStatus
import ish.oncourse.server.api.v1.model.MessageTypeDTO
import ish.oncourse.server.cayenne.Application
import ish.oncourse.server.cayenne.Article
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.CourseClassTutor
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.EntityRelationType
import ish.oncourse.server.cayenne.Invoice
import ish.oncourse.server.cayenne.InvoiceLine
import ish.oncourse.server.cayenne.Lead
import ish.oncourse.server.cayenne.Membership
import ish.oncourse.server.cayenne.Message
import ish.oncourse.server.cayenne.MessagePerson
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentOut
import ish.oncourse.server.cayenne.Payslip
import ish.oncourse.server.cayenne.ProductItem
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.cayenne.Tutor
import ish.oncourse.server.cayenne.Voucher
import ish.oncourse.server.cayenne.WaitingList
import ish.oncourse.server.cayenne.glue.CayenneDataObject
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.exp.Property

import java.util.function.Function

@CompileDynamic
class MessageFunctions {

    static final Map<String, List<String>> EMAIL_ENTITIES = [
            (ProductItem.simpleName.toLowerCase()): [ProductItem.simpleName.toLowerCase(),
                                                     Article.simpleName.toLowerCase(),
                                                     Membership.simpleName.toLowerCase(),
                                                     Voucher.simpleName.toLowerCase()],
    ]

    private static final Map<String, Map<String, Property<Long>>> ENTITY_TRANSFORMATION = [
            "CourseClass" : [
                    "Contact": Contact.STUDENT.dot(Student.ENROLMENTS).dot(Enrolment.COURSE_CLASS).dot(CourseClass.ID),
                    "Enrolment" : Enrolment.COURSE_CLASS.dot(CourseClass.ID),
                    "CourseClassTutor": CourseClassTutor.COURSE_CLASS.dot(CourseClass.ID)
            ],
            "ProductItem" : [
                    "Contact": Contact.PRODUCT_ITEMS.dot(ProductItem.ID),
                    "Voucher" : ProductItem.ID,
                    "Membership": ProductItem.ID,
                    "Article": ProductItem.ID
            ],
            "Enrolment" : ["Contact" : Contact.STUDENT.dot(Student.ENROLMENTS).dot(Enrolment.ID)],
            "Invoice" : ["Contact" : Contact.INVOICES.dot(Invoice.ID)],
            "Lead" : ["Contact" : Contact.LEADS.dot(Lead.ID)],
            "Application" : ["Contact" : Contact.STUDENT.dot(Student.APPLICATIONS).dot(Application.ID)],
            "Student" : ["Contact" : Contact.STUDENT.dot(Student.ID)],
            "Tutor" : ["Contact" : Contact.TUTOR.dot(Tutor.ID)],
            "PaymentIn" : ["Contact" : Contact.PAYMENTS_IN.dot(PaymentIn.ID)],
            "PaymentOut" : ["Contact" : Contact.PAYMENTS_OUT.dot(PaymentOut.ID)],
            "WaitingList" : ["Contact" :  Contact.STUDENT.dot(Student.WAITING_LISTS).dot(WaitingList.ID)],
            "Payslip" : ["Contact" :  Contact.PAYSLIPS.dot(Payslip.ID)]
    ]

    static Function<CayenneDataObject, Contact> getRecipientFunction(Class<? extends CayenneDataObject> clazz) {
        switch (clazz) {
            case Invoice:
                return { e -> (e as Invoice).contact }
            case Application:
                return { e -> (e as Application).student.contact }
            case Contact:
                return { e -> e }
            case Student:
                return { e -> (e as Student).contact }
            case Tutor:
                return { e -> (e as Tutor).contact }
            case CourseClassTutor:
                return { e -> (e as CourseClassTutor).tutor.contact }
            case Enrolment:
                return { e -> (e as Enrolment).student.contact }
            case PaymentIn:
                return { e -> (e as PaymentIn).payer }
            case PaymentOut:
                return { e -> (e as PaymentOut).payee }
            case ProductItem:
            case Article:
            case Membership:
                return { e -> (e as ProductItem).contact ?: (e as ProductItem).invoiceLine.invoice.contact }
            case Voucher:
                return { e -> (e as Voucher).redeemableBy ?: (e as Voucher).invoiceLine.invoice.contact }
            case WaitingList:
                return { e -> (e as WaitingList).student.contact }
            case Payslip:
                return { e -> (e as Payslip).contact }
            case Lead:
                return { e -> (e as Lead).customer }
            default:
                null
        }
    }


    static List<Contact> getRecipientsListFromEntity(CayenneDataObject entity) {
        List<Contact> recipients = new ArrayList<>()
        if (entity instanceof CourseClass) {

            entity.enrolments
                    .findAll { [EnrolmentStatus.IN_TRANSACTION, EnrolmentStatus.SUCCESS].contains(it.status) }
                    .each { enrolment ->
                        recipients.addAll(enrolment.student.contact.collect() as List<Contact>)
                    }

            entity.tutorRoles.each { courseClassTutor ->
                recipients.addAll(courseClassTutor.tutor.contact.collect() as List<Contact>)
            }
        } else {
            recipients = getRecipientFunction(entity.class).apply(entity).collect() as List<Contact>
        }
        recipients
    }


    static Property<Long> getFindContactProperty(String entityName) {
        switch (entityName) {
            case Invoice.ENTITY_NAME:
                return Contact.INVOICES.dot(Invoice.ID)
            case Application.ENTITY_NAME:
                return Contact.STUDENT.dot(Student.APPLICATIONS).dot(Application.ID)
            case Contact.ENTITY_NAME:
                return Contact.ID
            case Enrolment.ENTITY_NAME:
                return Contact.STUDENT.dot(Student.ENROLMENTS).dot(Enrolment.ID)
            case CourseClass.ENTITY_NAME:
                return Contact.STUDENT.dot(Student.ENROLMENTS).dot(Enrolment.COURSE_CLASS).dot(CourseClass.ID)
            case PaymentIn.ENTITY_NAME:
                return Contact.PAYMENTS_IN.dot(PaymentIn.ID)
            case PaymentOut.ENTITY_NAME:
                return Contact.PAYMENTS_OUT.dot(PaymentOut.ID)
            case ProductItem.ENTITY_NAME:
            case Article.ENTITY_NAME:
            case Membership.ENTITY_NAME:
                return Contact.INVOICES.dot(Invoice.INVOICE_LINES).dot(InvoiceLine.PRODUCT_ITEMS).dot(ProductItem.ID)
            case Voucher.ENTITY_NAME:
                return Contact.INVOICES.dot(Invoice.INVOICE_LINES).dot(InvoiceLine.VOUCHER_PAYMENT_IN).dot(Voucher.ID)
            case WaitingList.ENTITY_NAME:
                return Contact.STUDENT.dot(Student.WAITING_LISTS).dot(WaitingList.ID)
            case Payslip.ENTITY_NAME:
                return Contact.PAYSLIPS.dot(WaitingList.ID)
            case Lead.ENTITY_NAME:
                return Contact.LEADS.dot(Lead.ID)
            default:
                null
        }
    }

    static Property<Long> getEntityTransformationProperty(String entityName, String templateEntityName) {
        Property<Long> property = Property.create("id", Long)
        if (entityName != "Contact" && entityName != templateEntityName) {
            property = ENTITY_TRANSFORMATION.get(entityName)?.get(templateEntityName)
        }
        property
    }
}
