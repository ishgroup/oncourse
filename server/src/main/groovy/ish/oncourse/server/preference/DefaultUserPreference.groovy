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

package ish.oncourse.server.preference

import groovy.transform.CompileStatic
import ish.oncourse.server.api.v1.model.ColumnDTO
import ish.oncourse.server.api.v1.model.ColumnTypeDTO
import ish.oncourse.server.api.v1.model.LayoutTypeDTO
import ish.oncourse.server.api.v1.model.SortingDTO
import ish.oncourse.server.api.v1.model.TableModelDTO
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.AccountTransaction
import ish.oncourse.server.cayenne.Application
import ish.oncourse.server.cayenne.ArticleProduct
import ish.oncourse.server.cayenne.Assessment
import ish.oncourse.server.cayenne.AssessmentClass
import ish.oncourse.server.cayenne.AssessmentSubmission
import ish.oncourse.server.cayenne.Audit
import ish.oncourse.server.cayenne.Banking
import ish.oncourse.server.cayenne.Certificate
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.CorporatePass
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.CourseClass
import ish.oncourse.server.cayenne.DefinedTutorRole
import ish.oncourse.server.cayenne.Discount
import ish.oncourse.server.cayenne.Document
import ish.oncourse.server.cayenne.DocumentVersion
import ish.oncourse.server.cayenne.Enrolment
import ish.oncourse.server.cayenne.FieldConfiguration
import ish.oncourse.server.cayenne.Invoice
import ish.oncourse.server.cayenne.InvoiceLine
import ish.oncourse.server.cayenne.Lead
import ish.oncourse.server.cayenne.MembershipProduct
import ish.oncourse.server.cayenne.Message
import ish.oncourse.server.cayenne.Module
import ish.oncourse.server.cayenne.Outcome
import ish.oncourse.server.cayenne.PayRate
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentMethod
import ish.oncourse.server.cayenne.PaymentOut
import ish.oncourse.server.cayenne.Payslip
import ish.oncourse.server.cayenne.PriorLearning
import ish.oncourse.server.cayenne.Product
import ish.oncourse.server.cayenne.ProductItem
import ish.oncourse.server.cayenne.Qualification
import ish.oncourse.server.cayenne.Room
import ish.oncourse.server.cayenne.Script
import ish.oncourse.server.cayenne.Site
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.cayenne.Survey
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.server.cayenne.Tutor
import ish.oncourse.server.cayenne.VoucherProduct
import ish.oncourse.server.cayenne.WaitingList
import ish.oncourse.server.entity.mixins.ApplicationMixin
import ish.oncourse.server.entity.mixins.BankingMixin
import ish.oncourse.server.entity.mixins.PaymentInMixin

@CompileStatic
class DefaultUserPreference {

    private static final Integer W100 = 100
    private static final Integer W200 = 200
    private static final Integer W300 = 300
    private static final Integer W400 = 400

    private static final TableModelDTO AUDIT_MODEL = new TableModelDTO().with {
        it.columns = [
                new ColumnDTO(title: 'User Name', attribute: Audit.SYSTEM_USER.dot('fullName').name, sortable: true,
                        width: W200, visible: true, sortFields: [Audit.SYSTEM_USER.dot(SystemUser.LAST_NAME).name, Audit.SYSTEM_USER.dot(SystemUser.FIRST_NAME).name]),
                new ColumnDTO(title: 'Datetime', attribute: Audit.CREATED.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.DATE),
                new ColumnDTO(title: 'Entity identifier', attribute: Audit.ENTITY_IDENTIFIER.name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Action', attribute: Audit.ACTION.name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Entity id', attribute: Audit.ENTITY_ID.name, sortable: true, width: W100, visible: true)
        ]
        it.sortings = [
                new SortingDTO(attribute: Audit.CREATED.name, ascending: false)
        ]
        it.layout = LayoutTypeDTO.THREE_COLUMN
        it.filterColumnWidth = W200
        it
    }

    private static final TableModelDTO SCRIPT_MODEL = new TableModelDTO().with {
        it.columns = [
                new ColumnDTO(title: 'Name', attribute: Script.NAME.name, sortable: true, width: W400, visible: true),
                new ColumnDTO(title: 'Enabled', attribute: Script.ENABLED.name, sortable: true, width: W100, visible: true, type: ColumnTypeDTO.BOOLEAN),
                new ColumnDTO(title: 'Next run', attribute: 'nextRun', sortable: false, width: W300, visible: true),
        ]
        it.sortings = [
                new SortingDTO(attribute: Script.NAME.name, ascending: true)
        ]
        it.layout = LayoutTypeDTO.THREE_COLUMN
        it.filterColumnWidth = W200
        it
    }

    private static final TableModelDTO QUAL_MODEL = new TableModelDTO().with {
        it.columns = [
                new ColumnDTO(title: 'Code', attribute: Qualification.NATIONAL_CODE.name, sortable: true, width: W100, visible: true),
                new ColumnDTO(title: 'Title', attribute: Qualification.TITLE.name, sortable: true, width: W100, visible: true),
                new ColumnDTO(title: 'Level', attribute: Qualification.LEVEL.name, sortable: true, width: W100, visible: true),
                new ColumnDTO(title: 'Hours', attribute: Qualification.NOMINAL_HOURS.name, sortable: true, width: W100, visible: true),
                new ColumnDTO(title: 'Offered', attribute: Qualification.IS_OFFERED.name, sortable: true, width: W100, visible: true, type: ColumnTypeDTO.BOOLEAN),
        ]
        it.sortings = [
                new SortingDTO(attribute: Qualification.NATIONAL_CODE.name, ascending: true)
        ]
        it.layout = LayoutTypeDTO.THREE_COLUMN
        it.filterColumnWidth = W200
        it
    }

    private static final TableModelDTO MODULE_MODEL = new TableModelDTO().with {
        it.columns = [
                new ColumnDTO(title: 'Code', attribute: Module.NATIONAL_CODE.name, sortable: true, width: W100, visible: true),
                new ColumnDTO(title: 'Title', attribute: Module.TITLE.name, sortable: true, width: W100, visible: true),
                new ColumnDTO(title: 'Is offered', attribute: Module.IS_OFFERED.name, sortable: true, width: W100, visible: true, type: ColumnTypeDTO.BOOLEAN),
        ]
        it.sortings = [
                new SortingDTO(attribute: Module.NATIONAL_CODE.name, ascending: true)
        ]
        it.layout = LayoutTypeDTO.THREE_COLUMN
        it.filterColumnWidth = W200
        it
    }

    private static final TableModelDTO SITE_MODEL = new TableModelDTO().with {
        it.columns = [
                new ColumnDTO(title: 'Name', attribute: Site.NAME.name, sortable: true, width: W100, visible: true),
                new ColumnDTO(title: 'Country', attribute: Site.COUNTRY.dot("name").name, sortable: true, width: W100, visible: false),
                new ColumnDTO(title: 'State', attribute: Site.STATE.name, sortable: true, width: W100, visible: false),
                new ColumnDTO(title: 'Suburb', attribute: Site.SUBURB.name, sortable: true, width: W100, visible: true),
                new ColumnDTO(title: 'Street', attribute: Site.STREET.name, sortable: true, width: W200, visible: false),
                new ColumnDTO(title: 'Postcode', attribute: Site.POSTCODE.name, sortable: true, width: W100, visible: true),
                new ColumnDTO(title: 'Local timezone', attribute: Site.LOCAL_TIMEZONE.name, sortable: true, width: W200, visible: false),
                new ColumnDTO(title: 'Administration centre', attribute: Site.IS_ADMINISTRATION_CENTRE.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.BOOLEAN),
                new ColumnDTO(title: 'Shown on web', attribute: Site.IS_SHOWN_ON_WEB.name, sortable: true, width: W200, visible: false, type: ColumnTypeDTO.BOOLEAN),
                new ColumnDTO(title: 'Is virtual', attribute: Site.IS_VIRTUAL.name, sortable: true, width: W200, visible: false, type: ColumnTypeDTO.BOOLEAN)
        ]
        it.sortings = [
                new SortingDTO(attribute: Site.NAME.name, ascending: true)
        ]
        it.layout = LayoutTypeDTO.THREE_COLUMN
        it.filterColumnWidth = W200
        it
    }

    private static final TableModelDTO ROOM_MODEL = new TableModelDTO().with {
        it.columns = [
                new ColumnDTO(title: 'Name', attribute: Room.NAME.name, sortable: true, width: W100, visible: true),
                new ColumnDTO(title: 'Site', attribute: Room.SITE.dot("name").name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Seated capacity', attribute: Room.SEATED_CAPACITY.name, sortable: true, width: W200, visible: true)
        ]
        it.sortings = [
                new SortingDTO(attribute: Room.NAME.name, ascending: true)
        ]
        it.layout = LayoutTypeDTO.THREE_COLUMN
        it.filterColumnWidth = W200
        it
    }

    private static final TableModelDTO INVOICE_MODEL = new TableModelDTO().with {
        it.columns = [
                new ColumnDTO(title: 'Invoice number', attribute: Invoice.INVOICE_NUMBER.name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Source', attribute: Invoice.SOURCE.name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Date due', attribute: Invoice.DATE_DUE.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.DATE),
                new ColumnDTO(title: 'Name', attribute: Invoice.CONTACT.dot(Contact.FULL_NAME_KEY).name, sortable: true,
                        width: W200, visible: true, sortFields: [CorporatePass.CONTACT.dot(Contact.LAST_NAME).name,
                                                                 CorporatePass.CONTACT.dot(Contact.FIRST_NAME).name,
                                                                 CorporatePass.CONTACT.dot(Contact.MIDDLE_NAME).name]),
                new ColumnDTO(title: 'Owing', attribute: Invoice.AMOUNT_OWING.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.MONEY),
                new ColumnDTO(title: 'Total', attribute: Invoice.TOTAL_INC_TAX_KEY, sortable: false, width: W200, visible: true, type: ColumnTypeDTO.MONEY),
                new ColumnDTO(title: 'Overdue', attribute: Invoice.OVERDUE.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.MONEY),
                new ColumnDTO(title: 'Invoice Date', attribute: Invoice.INVOICE_DATE.name, sortable: true, width: W200, visible: false, type: ColumnTypeDTO.DATE)
        ]
        it.sortings = [
                new SortingDTO(attribute: Invoice.INVOICE_NUMBER.name, ascending: true)
        ]
        it.layout = LayoutTypeDTO.THREE_COLUMN
        it.filterColumnWidth = W200
        it
    }

    private static final TableModelDTO PAYMENT_IN_MODEL = new TableModelDTO().with {
        it.columns = [
                new ColumnDTO(title: 'Source', attribute: PaymentIn.SOURCE.name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Type', attribute: PaymentIn.PAYMENT_METHOD.dot(PaymentMethod.NAME).name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Status', attribute: PaymentInMixin.DISPLAY_STATYS,  sortFields: [PaymentIn.STATUS.name], sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Name', attribute: PaymentIn.PAYER.dot(Contact.FULL_NAME_KEY).name, sortable: true,
                        width: W200, visible: true, sortFields: [PaymentIn.PAYER.dot(Contact.LAST_NAME).name,
                                                                 PaymentIn.PAYER.dot(Contact.FIRST_NAME).name,
                                                                 PaymentIn.PAYER.dot(Contact.MIDDLE_NAME).name]),
                new ColumnDTO(title: 'Amount', attribute: PaymentIn.AMOUNT.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.MONEY),
                new ColumnDTO(title: 'Account', attribute: PaymentIn.ACCOUNT_IN.dot(Account.ACCOUNT_CODE.name).name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Date paid', attribute: PaymentIn.PAYMENT_DATE.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.DATE),
        ]
        it.sortings = [
                new SortingDTO(attribute: PaymentIn.SOURCE.name, ascending: true)
        ]
        it.layout = LayoutTypeDTO.THREE_COLUMN
        it.filterColumnWidth = W200
        it
    }

    private static final TableModelDTO PAYMENT_OUT_MODEL = new TableModelDTO().with {
        it.columns = [
                new ColumnDTO(title: 'Type', attribute: PaymentOut.PAYMENT_METHOD.dot(PaymentMethod.NAME).name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Payee name', attribute: PaymentOut.PAYEE.dot(Contact.FULL_NAME_KEY).name, sortable: true,
                        width: W200, visible: true, sortFields: [PaymentOut.PAYEE.dot(Contact.LAST_NAME).name,
                                                                 PaymentOut.PAYEE.dot(Contact.FIRST_NAME).name,
                                                                 PaymentOut.PAYEE.dot(Contact.MIDDLE_NAME).name]),
                new ColumnDTO(title: 'Amount', attribute: PaymentOut.AMOUNT.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.MONEY),
                new ColumnDTO(title: 'Date paid', attribute: PaymentOut.PAYMENT_DATE.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.DATE),
                new ColumnDTO(title: 'Banked', attribute:  PaymentOut.BANKING.dot(Banking.SETTLEMENT_DATE).name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.DATE),
                new ColumnDTO(title: 'Status', attribute: PaymentOut.STATUS.name, sortable: true, width: W200, visible: true),

        ]
        it.sortings = [
                new SortingDTO(attribute: PaymentIn.PAYMENT_DATE.name, ascending: true)
        ]
        it.layout = LayoutTypeDTO.THREE_COLUMN
        it.filterColumnWidth = W200
        it
    }

    private static final TableModelDTO TRANSACTION_MODEL = new TableModelDTO().with {
        it.columns = [
                new ColumnDTO(title: 'Transaction date', attribute: AccountTransaction.TRANSACTION_DATE.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.DATE),
                new ColumnDTO(title: 'Account code', attribute: AccountTransaction.ACCOUNT.dot(Account.ACCOUNT_CODE.name).name, sortable: true, width: W100, visible: true),
                new ColumnDTO(title: 'Account description', attribute: AccountTransaction.ACCOUNT.dot(Account.DESCRIPTION.name).name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Account type', attribute: AccountTransaction.ACCOUNT.dot(Account.TYPE.name).name, sortable: false, width: W100, visible: true),
                new ColumnDTO(title: 'Amount', attribute: AccountTransaction.AMOUNT.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.MONEY),
                new ColumnDTO(title: 'Source', attribute: AccountTransaction.TABLE_NAME.name, sortable: true, width: W100, visible: true),
                new ColumnDTO(title: 'Created', attribute: AccountTransaction.CREATED_ON.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.DATETIME),
        ]
        it.sortings = [
                new SortingDTO(attribute: AccountTransaction.TRANSACTION_DATE.name, ascending: true)
        ]
        it.layout = LayoutTypeDTO.THREE_COLUMN
        it.filterColumnWidth = W200
        it
    }

    private static final TableModelDTO PAYSLIP_MODEL = new TableModelDTO().with {
        it.columns = [
                new ColumnDTO(title: 'Name', attribute: Payslip.CONTACT.dot(Contact.FULL_NAME_KEY).name, sortable: true,
                    width: W200, visible: true, sortFields: [CorporatePass.CONTACT.dot(Contact.LAST_NAME).name,
                                                             CorporatePass.CONTACT.dot(Contact.FIRST_NAME).name,
                                                             CorporatePass.CONTACT.dot(Contact.MIDDLE_NAME).name]),
                new ColumnDTO(title: 'Payroll reference number', attribute: Payslip.CONTACT.dot(Contact.TUTOR).dot(Tutor.PAYROLL_REF).name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Created', attribute: Payslip.CREATED_ON.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.DATE),
                new ColumnDTO(title: 'Status', attribute: Payslip.STATUS.name, sortable: true, width: W200, visible: true)
        ]
        it.sortings = [
            new SortingDTO(attribute: Payslip.CREATED_ON.name, ascending: true)
        ]
        it.layout = LayoutTypeDTO.THREE_COLUMN
        it.filterColumnWidth = W200
        it
    }

    private static final TableModelDTO DOCUMENT_MODEL = new TableModelDTO().with() {
        it.columns = [
                new ColumnDTO(title: 'Link', attribute: Document.LINK_PROPERTY, sortable: false, width: W200, visible: true),
                new ColumnDTO(title: 'Document name', attribute: Document.NAME.name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Date added', attribute: Document.ADDED.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.DATE),
                new ColumnDTO(title: 'Size', attribute: Document.CURRENT_VERSION_PROPERTY + "." + DocumentVersion.BYTE_SIZE.name, sortable: false, width: W200, visible: true),
                new ColumnDTO(title: 'Security level', attribute: Document.WEB_VISIBILITY.name, sortable: true, width: W100, visible: true),
                new ColumnDTO(title: 'File name', attribute: Document.CURRENT_VERSION_PROPERTY + "." + DocumentVersion.FILE_NAME.name, sortable: false, width: W100, visible: true),
                new ColumnDTO(title: 'Type', attribute: Document.CURRENT_VERSION_PROPERTY + "." + DocumentVersion.MIME_TYPE.name, sortable: false, width: W100, visible: true),
                new ColumnDTO(title: 'Active', attribute: Document.ACTIVE_PROPERTY, sortable: false, width: W100, visible: false, system: true, type: ColumnTypeDTO.BOOLEAN)
        ]
        it.sortings = [
                new SortingDTO(attribute: Document.NAME.name, ascending: true)
        ]
        it.layout = LayoutTypeDTO.THREE_COLUMN
        it.filterColumnWidth = W200
        it
    }

    private static final TableModelDTO ACCOUNT_MODEL = new TableModelDTO().with {
        it.columns = [
                new ColumnDTO(title: 'Code', attribute: Account.ACCOUNT_CODE.name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Enabled', attribute: Account.IS_ENABLED.name, sortable: true, width: W100, visible: true, type: ColumnTypeDTO.BOOLEAN),
                new ColumnDTO(title: 'Type', attribute: Account.TYPE.name, sortable: true, width: W100, visible: true),
                new ColumnDTO(title: 'Description', attribute: Account.DESCRIPTION.name, sortable: true, width: W200, visible: true)
        ]
        it.sortings = [
            new SortingDTO(attribute: Account.ACCOUNT_CODE.name, ascending: true)
        ]
        it.layout = LayoutTypeDTO.THREE_COLUMN
        it.filterColumnWidth = W200
        it
    }

    private static final TableModelDTO DISCOUNT_MODEL = new TableModelDTO().with {
        it.columns = [
                new ColumnDTO(title: 'Valid from', attribute: Discount.VALID_FROM.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.DATE),
                new ColumnDTO(title: 'Valid to', attribute: Discount.VALID_TO.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.DATE),
                new ColumnDTO(title: 'Name', attribute: Discount.NAME.name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Promotional code', attribute: Discount.CODE.name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Discount value', attribute: Discount.DISCOUNT_DOLLAR.name, sortable: true, width: W100, visible: true, type: ColumnTypeDTO.MONEY),
                new ColumnDTO(title: 'Discount percent', attribute: Discount.DISCOUNT_PERCENT.name, sortable: true, width: W100, visible: true, type: ColumnTypeDTO.PERCENT),
                new ColumnDTO(title: 'Discount type', attribute: Discount.DISCOUNT_TYPE.name, sortable: true, width: W100, visible: true)
        ]
        it.sortings = [
                new SortingDTO(attribute: Discount.VALID_FROM.name, ascending: true)
        ]
        it.layout = LayoutTypeDTO.THREE_COLUMN
        it.filterColumnWidth = W200
        it
    }

    private static final TableModelDTO BANKING_MODEL = new TableModelDTO().with {
        it.columns = [
                new ColumnDTO(title: 'Reconciled', attribute: BankingMixin.RECONCILED_STATUS, sortable: false, width: W200, visible: true),
                new ColumnDTO(title: 'Date', attribute: Banking.SETTLEMENT_DATE.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.DATE),
                new ColumnDTO(title: 'Type', attribute: Banking.TYPE.name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Site', attribute: Banking.ADMIN_SITE.dot(Site.NAME).name, sortable: false, width: W200, visible: true),
                new ColumnDTO(title: 'User', attribute: Banking.CREATED_BY.dot(SystemUser.LOGIN).name, sortable: false, width: W200, visible: true),
                new ColumnDTO(title: 'Total', attribute: BankingMixin.TOTAL, sortable: false, width: W200, visible: true, type: ColumnTypeDTO.MONEY)
        ]
        it.sortings = [
                new SortingDTO(attribute: Banking.SETTLEMENT_DATE.name, ascending: false)
        ]
        it.layout = LayoutTypeDTO.THREE_COLUMN
        it.filterColumnWidth = W200
        it
    }

    private static final TableModelDTO CORPORATE_PASS_MODEL = new TableModelDTO().with {
        it.columns = [
                new ColumnDTO(title: 'Contact', attribute: CorporatePass.CONTACT.dot(Contact.FULL_NAME_KEY).name, sortable: false,
                        width: W200, visible: true, sortFields: [CorporatePass.CONTACT.dot(Contact.LAST_NAME).name,
                                                                 CorporatePass.CONTACT.dot(Contact.FIRST_NAME).name,
                                                                 CorporatePass.CONTACT.dot(Contact.MIDDLE_NAME).name]),
                new ColumnDTO(title: 'Email to', attribute: 'email', sortable: false, width: W200, visible: true),
                new ColumnDTO(title: 'Expiry date', attribute: CorporatePass.EXPIRY_DATE.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.DATE),
                new ColumnDTO(title: 'Used', attribute: 'timesUsed', sortable: false, width: W100, visible: true)
        ]
        it.sortings = [
                new SortingDTO(attribute: CorporatePass.EXPIRY_DATE.name, ascending: true)
        ]
        it.layout = LayoutTypeDTO.THREE_COLUMN
        it.filterColumnWidth = W200
        it
    }

    private static final TableModelDTO WAIT_LIST_MODEL = new TableModelDTO().with {
        it.columns = [
                new ColumnDTO(title: 'Created', attribute: WaitingList.CREATED_ON.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.DATE),
                new ColumnDTO(title: 'Student', attribute: WaitingList.STUDENT.dot(Student.CONTACT).dot(Contact.FULL_NAME_KEY).name, sortable: true,
                        width: W300, visible: true, sortFields: [WaitingList.STUDENT.dot(Student.CONTACT).dot(Contact.LAST_NAME).name,
                                                                 WaitingList.STUDENT.dot(Student.CONTACT).dot(Contact.FIRST_NAME).name,
                                                                 WaitingList.STUDENT.dot(Student.CONTACT).dot(Contact.MIDDLE_NAME).name]),
                new ColumnDTO(title: 'Course name', attribute: WaitingList.COURSE.dot(Course.NAME).name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Course code', attribute: WaitingList.COURSE.dot(Course.CODE).name, sortable: true, width: W100, visible: true),
                new ColumnDTO(title: 'Student requirements', attribute: WaitingList.STUDENT_NOTES.name, sortable: true, width: W200, visible: true)
        ]
        it.sortings = [new SortingDTO(attribute: WaitingList.CREATED_ON.name, ascending: false)]
        it.layout = LayoutTypeDTO.THREE_COLUMN
        it.filterColumnWidth = W200
        it
    }

    private static final TableModelDTO LEAD_MODEL = new TableModelDTO().with {
        it.columns = [
                new ColumnDTO(title: 'Created', attribute: Lead.CREATED_ON.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.DATETIME),
                new ColumnDTO(title: 'Customer', attribute: Lead.CUSTOMER.dot(Contact.FULL_NAME_KEY).name, sortable: true,
                        width: W300, visible: true, sortFields: [Lead.CUSTOMER.dot(Contact.LAST_NAME).name,
                                                                 Lead.CUSTOMER.dot(Contact.FIRST_NAME).name,
                                                                 Lead.CUSTOMER.dot(Contact.MIDDLE_NAME).name]),
                new ColumnDTO(title: 'Estimated value', attribute: Lead.ESTIMATED_VALUE.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.MONEY),
                new ColumnDTO(title: 'Next action on', attribute: Lead.NEXT_ACTION_ON.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.DATETIME),
                new ColumnDTO(title: 'Status', attribute: Lead.STATUS.name, sortable: true, width: W100, visible: true, type: ColumnTypeDTO.BOOLEAN),
                new ColumnDTO(title: 'Student notes', attribute: Lead.STUDENT_NOTES.name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Assigned to', attribute: Lead.ASSIGNED_TO.dot(SystemUser.EMAIL).name, sortable: true, width: W200, visible: true)
        ]
        it.sortings = [new SortingDTO(attribute: Lead.CREATED_ON.name, ascending: false)]
        it.layout = LayoutTypeDTO.THREE_COLUMN
        it.filterColumnWidth = W200
        it
    }

    private static final TableModelDTO APPLICATION_MODEL = new TableModelDTO().with {
        it.columns = [
                new ColumnDTO(title: 'Source', attribute: Application.SOURCE.name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Student', attribute: Application.STUDENT.dot(Student.CONTACT).dot(Contact.FULL_NAME_KEY).name, sortable: true,
                        width: W300, visible: true, sortFields: [Application.STUDENT.dot(Student.CONTACT).dot(Contact.LAST_NAME).name,
                                                                 Application.STUDENT.dot(Student.CONTACT).dot(Contact.FIRST_NAME).name,
                                                                 Application.STUDENT.dot(Student.CONTACT).dot(Contact.MIDDLE_NAME).name]),
                new ColumnDTO(title: 'Course', attribute: Application.COURSE.dot(Course.NAME).name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Date of application', attribute: Application.CREATED_ON.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.DATE),
                new ColumnDTO(title: 'Status', attribute: ApplicationMixin.DISPLAY_STATUS, sortFields:[Application.STATUS.name], sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Fee Override', attribute: Application.FEE_OVERRIDE.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.MONEY),
        ]
        it.sortings = [
                new SortingDTO(attribute: Application.SOURCE.name, ascending: true)
        ]
        it.layout = LayoutTypeDTO.THREE_COLUMN
        it.filterColumnWidth = W200
        it
    }

    private static final TableModelDTO ARTICLE_PRODUCT_MODEL = new TableModelDTO().with {
        it.columns = [
                new ColumnDTO(title: 'Name', attribute: ArticleProduct.NAME.name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Code', attribute: ArticleProduct.SKU.name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Price', attribute: ArticleProduct.PRICE_INC_TAX_PROPERTY, sortable: false, width: W200, visible: true, type: ColumnTypeDTO.MONEY),
                new ColumnDTO(title: 'Number sold', attribute: ArticleProduct.NUMBER_SOLD_PROPERTY, sortable: false, width: W200, visible: true),
        ]
        it.sortings = [
                new SortingDTO(attribute: ArticleProduct.NAME.name, ascending: true)
        ]
        it.layout = LayoutTypeDTO.THREE_COLUMN
        it.filterColumnWidth = W200
        it
    }

    private static final TableModelDTO COURSE_MODEL = new TableModelDTO().with() {
        it.columns = [
                new ColumnDTO(title: 'Name', attribute: Course.NAME.name, sortable: true, width: W400, visible: true),
                new ColumnDTO(title: 'Code', attribute: Course.CODE.name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Field', attribute: Course.FIELD_OF_EDUCATION.name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Qualification',
                        attribute: Course.QUALIFICATION.dot(Qualification.NATIONAL_CODE).name,
                        sortable: true,
                        sortFields: [Course.QUALIFICATION.outer().dot(Qualification.NATIONAL_CODE).name],
                        width: W200, visible: true),
                new ColumnDTO(title: 'Current classes',
                        attribute: Course.CURRENT_CLASS_COUNT_PROPERTY,
                        sortable: false,
                        width: W200,
                        visible: true),
                new ColumnDTO(title: 'Currently offered',
                        attribute: Course.CURRENTLY_OFFERED.name,
                        sortable: true,
                        width: W100,
                        type: ColumnTypeDTO.BOOLEAN,
                        visible: false,
                        system: true),
                new ColumnDTO(title: 'Shown on web',
                        attribute: Course.IS_SHOWN_ON_WEB.name,
                        sortable: true,
                        width: W100,
                        type: ColumnTypeDTO.BOOLEAN,
                        visible: false,
                        system: true),

        ]
        it.sortings = [
            new SortingDTO(attribute: Course.NAME.name, ascending: true)
        ]
        it.layout = LayoutTypeDTO.THREE_COLUMN
        it.filterColumnWidth = W200
        it
    }

    private static final TableModelDTO VOUCHER_PRODUCT_MODEL = new TableModelDTO().with() {
        it.columns = [
                new ColumnDTO(title: 'SKU', attribute: VoucherProduct.SKU.name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Name', attribute: VoucherProduct.NAME.name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Price', attribute: VoucherProduct.PRICE_EX_TAX.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.MONEY),
                new ColumnDTO(title: 'Online purchase', attribute: VoucherProduct.IS_WEB_VISIBLE.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.BOOLEAN),
                new ColumnDTO(title: 'Number sold', attribute: VoucherProduct.SOLD_VOUCHERS_COUNT, sortable: false, width: W200, visible: true),
        ]
        it.sortings = [
                new SortingDTO(attribute: VoucherProduct.SKU.name, ascending: true)
        ]
        it.layout = LayoutTypeDTO.THREE_COLUMN
        it.filterColumnWidth = W200
        it
    }

    private static final TableModelDTO MEMBERSHIP_PRODUCT_MODEL = new TableModelDTO().with() {
        it.columns = [
                new ColumnDTO(title: 'Name', attribute: MembershipProduct.NAME.name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Price', attribute: MembershipProduct.PRICE_INC_TAX_PROPERTY, sortFields: [MembershipProduct.PRICE_EX_TAX.name], sortable: true, width: W200, visible: true, type: ColumnTypeDTO.MONEY),
                new ColumnDTO(title: 'SKU', attribute: MembershipProduct.SKU.name, sortable: true, width: W200, visible: true),
        ]
        it.sortings = [
            new SortingDTO(attribute: MembershipProduct.NAME.name, ascending: true)
        ]
        it.layout = LayoutTypeDTO.THREE_COLUMN
        it.filterColumnWidth = W200
        it
    }

    private static final TableModelDTO CERTIFICATE_MODEL = new TableModelDTO().with() {
        it.columns = [
                new ColumnDTO(title: 'Student name',
                        attribute: Certificate.STUDENT.dot(Student.CONTACT).dot(Contact.FULL_NAME_KEY).name,
                        sortable: true,
                        width: W200,
                        visible: true,
                        sortFields: [Certificate.STUDENT.dot(Student.CONTACT).dot(Contact.LAST_NAME).name,
                                     Certificate.STUDENT.dot(Student.CONTACT).dot(Contact.FIRST_NAME).name,
                                     Certificate.STUDENT.dot(Student.CONTACT).dot(Contact.MIDDLE_NAME).name]),
                new ColumnDTO(title: 'Full qualification', attribute: Certificate.IS_QUALIFICATION.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.BOOLEAN),
                new ColumnDTO(title: 'Qualification code', attribute: Certificate.QUALIFICATION.dot(Qualification.NATIONAL_CODE).name, sortable: false, width: W200, visible: true),
                new ColumnDTO(title: 'Qualification level', attribute: Certificate.QUALIFICATION.dot(Qualification.LEVEL).name, sortable: false, width: W200, visible: true),
                new ColumnDTO(title: 'Qualification title', attribute: Certificate.QUALIFICATION.dot(Qualification.TITLE).name, sortable: false, width: W200, visible: true),
                new ColumnDTO(title: 'Certificate number', attribute: Certificate.CERTIFICATE_NUMBER.name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Awarded On', attribute: Certificate.AWARDED_ON.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.DATE),
                new ColumnDTO(title: 'Issued On', attribute: Certificate.ISSUED_ON.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.DATE),
                new ColumnDTO(title: 'Revoked On', attribute: Certificate.REVOKED_ON.name, sortable: true, width: W200, visible: false, type: ColumnTypeDTO.DATE),
        ]
        it.sortings = [
                new SortingDTO(attribute: Certificate.AWARDED_ON.name, ascending: true)
        ]
        it.layout = LayoutTypeDTO.THREE_COLUMN
        it.filterColumnWidth = W200
        it
    }

    private static final TableModelDTO STUDENT_FEEDBACK_MODEL = new TableModelDTO().with() {
        it.columns = [
                new ColumnDTO(title: 'Created on', attribute: Survey.CREATED_ON.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.DATE),
                new ColumnDTO(
                        title: 'Student name',
                        attribute:
                                Survey.ENROLMENT
                                        .dot(Enrolment.STUDENT)
                                        .dot(Student.CONTACT)
                                        .dot(Contact.FULL_NAME_KEY).name,
                        sortFields: [ Survey.ENROLMENT.dot(Enrolment.STUDENT).dot(Student.CONTACT).dot(Contact.LAST_NAME).name,
                                      Survey.ENROLMENT.dot(Enrolment.STUDENT).dot(Student.CONTACT).dot(Contact.FIRST_NAME).name],
                        sortable: true,
                        width: W200,
                        visible: true
                ),
                new ColumnDTO(
                        title: 'Course',
                        attribute:
                                Survey.ENROLMENT
                                        .dot(Enrolment.COURSE_CLASS)
                                        .dot(CourseClass.COURSE)
                                        .dot(Course.NAME).name,
                        sortable: true,
                        width: W200,
                        visible: true
                ),
                new ColumnDTO(
                        title: 'Class',
                        attribute:
                                Survey.ENROLMENT
                                        .dot(Enrolment.COURSE_CLASS)
                                        .dot(CourseClass.UNIQUE_CODE_PROPERTY).name,
                        sortFields: [Survey.ENROLMENT.dot(Enrolment.COURSE_CLASS).dot(CourseClass.COURSE).dot(Course.CODE).name,
                                     Survey.ENROLMENT.dot(Enrolment.COURSE_CLASS).dot(CourseClass.CODE).name],
                        sortable: true, width: W200,
                        visible: true
                ),
                new ColumnDTO(title: 'Net promoter score', attribute: Survey.NET_PROMOTER_SCORE.name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Field configuration', attribute: Survey.FIELD_CONFIGURATION.dot(FieldConfiguration.NAME).name, sortable: true, width: W200, visible: true),
        ]
        it.sortings = [
                new SortingDTO(attribute: Survey.CREATED_ON.name, ascending: true)
        ]
        it.layout = LayoutTypeDTO.THREE_COLUMN
        it.filterColumnWidth = W200
        it
    }

    private static final TableModelDTO SALE_MODEL = new TableModelDTO().with() {
        it.columns = [
                new ColumnDTO(title: 'Name', attribute: ProductItem.PRODUCT.dot(Product.NAME).name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Type', attribute: ProductItem.TYPE_STRING_DEFENITION, sortable: false, width: W200, visible: true),
                new ColumnDTO(title: 'SKU', attribute: ProductItem.PRODUCT.dot(Product.SKU).name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Expires', attribute: ProductItem.EXPIRY_DATE.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.DATE),
                new ColumnDTO(title: 'Status', attribute: ProductItem.DISPLAYABLE_STATUS, sortFields: [ProductItem.STATUS.name], sortable: true, width: W200, visible: true),
                new ColumnDTO(
                        title: 'Purchased by',
                        attribute: ProductItem.INVOICE_LINE.dot(InvoiceLine.INVOICE)
                                .dot(Invoice.CONTACT).dot(Contact.FULL_NAME_KEY).name,
                        sortable: true,
                        width: W200,
                        visible: true,
                        sortFields: [
                                ProductItem.INVOICE_LINE.dot(InvoiceLine.INVOICE).dot(Invoice.CONTACT).dot(Contact.LAST_NAME).name,
                                ProductItem.INVOICE_LINE.dot(InvoiceLine.INVOICE).dot(Invoice.CONTACT).dot(Contact.FIRST_NAME).name,
                                ProductItem.INVOICE_LINE.dot(InvoiceLine.INVOICE).dot(Invoice.CONTACT).dot(Contact.MIDDLE_NAME).name,
                        ]
                ),
                new ColumnDTO(title: 'Purchased on', attribute: ProductItem.CREATED_ON.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.DATE ),
        ]
        it.sortings = [
                new SortingDTO(attribute: ProductItem.PRODUCT.dot(Product.NAME).name, ascending: true)
        ]
        it.layout = LayoutTypeDTO.THREE_COLUMN
        it.filterColumnWidth = W200
        it
    }

    private static final OUTCOME_MODEL = new TableModelDTO().with() {
        it.columns = [
                new ColumnDTO(
                        title: 'Student name',
                        attribute: Outcome.STUDENT_NAME,
                        sortable: true,
                        width: W200,
                        visible: true,
                        sortFields: [Outcome.ENROLMENT.outer().dot(Enrolment.STUDENT).outer().dot(Student.CONTACT).outer().dot(Contact.LAST_NAME).name,
                                     Outcome.ENROLMENT.outer().dot(Enrolment.STUDENT).outer().dot(Student.CONTACT).outer().dot(Contact.FIRST_NAME).name,
                                     Outcome.PRIOR_LEARNING.outer().dot(PriorLearning.STUDENT).outer().dot(Student.CONTACT).outer().dot(Contact.LAST_NAME).name,
                                     Outcome.PRIOR_LEARNING.outer().dot(PriorLearning.STUDENT).outer().dot(Student.CONTACT).outer().dot(Contact.FIRST_NAME).name,
                                    ]
                ),
                new ColumnDTO(title: 'Course', attribute: Outcome.ENROLMENT.dot(Enrolment.COURSE_CLASS).dot(CourseClass.COURSE).dot(Course.NAME).name, sortable: true, width: W200, visible: true,
                        sortFields: [ Outcome.ENROLMENT.outer().dot(Enrolment.COURSE_CLASS).outer().dot(CourseClass.COURSE).outer().dot(Course.NAME).name ]),
                new ColumnDTO(title: 'Code',
                        attribute: Outcome.ENROLMENT.dot(Enrolment.COURSE_CLASS).dot(CourseClass.UNIQUE_CODE_PROPERTY).name,
                        sortable: true,
                        width: W200,
                        visible: true,
                        sortFields: [Outcome.ENROLMENT.outer().dot(Enrolment.COURSE_CLASS).outer().dot(CourseClass.COURSE).outer().dot(Course.CODE).name,
                                     Outcome.ENROLMENT.outer().dot(Enrolment.COURSE_CLASS).outer().dot(CourseClass.CODE).name]
                ),
                new ColumnDTO(title: 'National code', attribute: Outcome.MODULE.dot(Module.NATIONAL_CODE).name, sortable: true, width: W200, visible: false,
                        sortFields: [Outcome.MODULE.outer().dot(Module.NATIONAL_CODE).name], system: true ),
                new ColumnDTO(title: 'UOC name', attribute: Outcome.MODULE.dot(Module.TITLE).name, sortable: true, width: W200, visible: true,
                        sortFields: [Outcome.MODULE.outer().dot(Module.TITLE).name] ),
                new ColumnDTO(title: 'Status', attribute: Outcome.STATUS.name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Training plan start date', attribute: Outcome.TRAINING_PLAN_START_DATE_PROPERTY, sortable: false, width: W200, visible: true, type: ColumnTypeDTO.DATE),
                new ColumnDTO(title: 'Training plan end date', attribute: Outcome.TRAINING_PLAN_END_DATE_PROPERTY, sortable: false, width: W200, visible: true,  type: ColumnTypeDTO.DATE),
                new ColumnDTO(title: 'Start date', attribute: Outcome.START_DATE.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.DATE),
                new ColumnDTO(title: 'End date', attribute: Outcome.END_DATE.name, sortable: true, width: W200, visible: true,  type: ColumnTypeDTO.DATE),
                new ColumnDTO(title: 'Delivery mode', attribute: Outcome.DELIVERY_MODE.name, sortable: true, width: W200, visible: true)
        ]
        it.sortings = [
            new SortingDTO(attribute: Outcome.ENROLMENT.outer().dot(Enrolment.COURSE_CLASS).outer().dot(CourseClass.COURSE).outer().dot(Course.NAME).name, ascending: true)
        ]
        it.layout = LayoutTypeDTO.THREE_COLUMN
        it.filterColumnWidth = W200
        it
    }

    private static final ASSESSMENT_MODEL = new TableModelDTO().with() {
        it.columns = [
                new ColumnDTO(title: 'Code', attribute: Assessment.CODE.name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Name', attribute: Assessment.NAME.name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Active', attribute: Assessment.ACTIVE.name, sortable: true, width: W200, visible: false, system: true, type: ColumnTypeDTO.BOOLEAN),
        ]
        it.sortings = [
                new SortingDTO(attribute: Assessment.CODE.name, ascending: true)
        ]
        it.layout = LayoutTypeDTO.THREE_COLUMN
        it.filterColumnWidth = W200
        it
    }

    private static final ASSESSMENT_SUBMISSION_MODEL = new TableModelDTO().with() {
        it.columns = [
                new ColumnDTO(title: 'Student name',
                        attribute: AssessmentSubmission.STUDENT_NAME_PROPERTY, sortable: true, width: W200, visible: true,
                        sortFields: [
                                AssessmentSubmission.ENROLMENT.dot(Enrolment.STUDENT).dot(Student.CONTACT).dot(Contact.LAST_NAME).name,
                                AssessmentSubmission.ENROLMENT.dot(Enrolment.STUDENT).dot(Student.CONTACT).dot(Contact.FIRST_NAME).name,
                                AssessmentSubmission.ENROLMENT.dot(Enrolment.STUDENT).dot(Student.CONTACT).dot(Contact.MIDDLE_NAME).name
                        ]),
                new ColumnDTO(title: 'Class name',
                        attribute: AssessmentSubmission.CLASS_NAME_PROPERTY, sortable: true, width: W300, visible: true,
                        sortFields: [
                                AssessmentSubmission.ASSESSMENT_CLASS.dot(AssessmentClass.COURSE_CLASS).dot(CourseClass.COURSE).dot(Course.CODE).name,
                                AssessmentSubmission.ASSESSMENT_CLASS.dot(AssessmentClass.COURSE_CLASS).dot(CourseClass.CODE).name
                        ]
                ),
                new ColumnDTO(title: 'Assessment name',
                        attribute: AssessmentSubmission.ASSESSMENT_NAME_PROPERTY, sortable: true, width: W200, visible: true,
                        sortFields: [
                                AssessmentSubmission.ASSESSMENT_CLASS.dot(AssessmentClass.ASSESSMENT).dot(Assessment.CODE).name
                        ]
                ),
                new ColumnDTO(title: 'Submitted on', attribute: AssessmentSubmission.SUBMITTED_ON.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.DATETIME),
                new ColumnDTO(title: 'Marked on', attribute: AssessmentSubmission.MARKED_ON.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.DATETIME),
        ]
        it.sortings = [
                new SortingDTO(attribute: AssessmentSubmission.ENROLMENT.dot(Enrolment.ID).name, ascending: true)
        ]
        it.layout = LayoutTypeDTO.THREE_COLUMN
        it.filterColumnWidth = W200
        it
    }

    private static final ENROLMENT_MODEL = new TableModelDTO().with() {
        it.columns = [
                new ColumnDTO(title: 'Source', attribute: Enrolment.SOURCE.name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Name',
                        attribute: Enrolment.STUDENT.dot(Student.CONTACT).dot(Contact.FULL_NAME_KEY).name,
                        sortable: true, width: W200, visible: true,
                        sortFields: [Enrolment.STUDENT.dot(Student.CONTACT).dot(Contact.LAST_NAME).name,
                                     Enrolment.STUDENT.dot(Student.CONTACT).dot(Contact.FIRST_NAME).name,
                                     Enrolment.STUDENT.dot(Student.CONTACT).dot(Contact.MIDDLE_NAME).name]),
                new ColumnDTO(title: 'Class',
                        attribute: Enrolment.COURSE_CLASS.dot(CourseClass.UNIQUE_CODE_PROPERTY).name,
                        sortable: true,
                        width: W200,
                        visible: true,
                        sortFields: [Enrolment.COURSE_CLASS.dot(CourseClass.COURSE).dot(Course.CODE).name,
                                     Enrolment.COURSE_CLASS.dot(CourseClass.CODE).name]),
                new ColumnDTO(title: 'Course name', attribute: Enrolment.COURSE_CLASS.dot(CourseClass.COURSE).dot(Course.NAME).name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Status', sortable: true, width: W200, visible: true, attribute: Enrolment.DISPLAY_STATUS_PROP,
                        sortFields: [Enrolment.STATUS.name, Enrolment.COURSE_CLASS.outer().dot(CourseClass.END_DATE_TIME).name]),
                new ColumnDTO(title: 'Enrolled', attribute: Enrolment.CREATED_ON.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.DATE),

        ]
        it.sortings = [
                new SortingDTO(attribute: Enrolment.SOURCE.name, ascending: true)
        ]
        it.layout = LayoutTypeDTO.THREE_COLUMN
        it.filterColumnWidth = W200
        it
    }

    private static final PRIOR_LEARNING_MODEL =  new TableModelDTO().with {
        it.columns = [
                new ColumnDTO(title: 'Name', attribute: PriorLearning.STUDENT.dot(Student.CONTACT).dot(Contact.FULL_NAME_KEY).name, sortable: true,
                        width: W200, visible: true, sortFields: [PriorLearning.STUDENT.dot(Student.CONTACT).dot(Contact.LAST_NAME).name,
                                                                 PriorLearning.STUDENT.dot(Student.CONTACT).dot(Contact.FIRST_NAME).name,
                                                                 PriorLearning.STUDENT.dot(Student.CONTACT).dot(Contact.MIDDLE_NAME).name]),

                new ColumnDTO(title: 'External reference', attribute: PriorLearning.EXTERNAL_REF.name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Title', attribute: PriorLearning.TITLE.name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Training Org', attribute: PriorLearning.OUTCOME_ID_TRAINING_ORG.name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Qualification', attribute: PriorLearning.QUALIFICATION.dot(Qualification.NATIONAL_CODE).name, sortable: true, width: W200, visible: true,
                        sortFields: [PriorLearning.QUALIFICATION.outer().dot(Qualification.NATIONAL_CODE).name])


        ]
        it.sortings = [
                new SortingDTO(attribute: PriorLearning.TITLE.name, ascending: true)
        ]
        it.layout = LayoutTypeDTO.TWO_COLUMN
        it.filterColumnWidth = W200
        it
    }

    private static final MESSAGE_MODEL = new TableModelDTO().with() {
        it.columns = [
                new ColumnDTO(title: 'Date time', attribute: Message.CREATED_ON.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.DATE),
                new ColumnDTO(title: 'Sent by', attribute: Message.CREATED_BY.dot(SystemUser.LOGIN).name, sortable: false, width: W200, visible: true),
                new ColumnDTO(title: 'Recipients', attribute: Message.RECIPIENTS_STRING_PROPERTY, sortable: false, width: W200, visible: true),
                new ColumnDTO(title: 'SMS', attribute: Message.IS_SMS_PROPERTY, sortable: false, width: W200, visible: true, type: ColumnTypeDTO.BOOLEAN),
                new ColumnDTO(title: 'Email', attribute: Message.IS_EMAIL_PROPERTY, sortable: false, width: W200, visible: true, type: ColumnTypeDTO.BOOLEAN),
                new ColumnDTO(title: 'Post', attribute: Message.IS_POST_PROPERTY, sortable: false, width: W200, visible: true, type: ColumnTypeDTO.BOOLEAN),
                new ColumnDTO(title: 'Subject', attribute: Message.EMAIL_SUBJECT.name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Creator key', attribute: Message.CREATOR_KEY.name, sortable: true, width: W200, visible: true),
        ]
        it.sortings = [
                new SortingDTO(attribute: Message.CREATED_ON.name, ascending: true)
        ]
        it.layout = LayoutTypeDTO.THREE_COLUMN
        it.filterColumnWidth = W200
        it
    }

    private static final DEFINED_TUTOR_ROLE_MODEL = new TableModelDTO().with() {
        it.columns = [
                new ColumnDTO(title: 'Name', attribute: DefinedTutorRole.NAME.name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Description', attribute: DefinedTutorRole.DESCRIPTION.name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Active', attribute: DefinedTutorRole.ACTIVE.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.BOOLEAN),
                new ColumnDTO(title: 'Rate', attribute: DefinedTutorRole.CURRENT_PAYRATE + "." + PayRate.RATE.name, sortable: false, width: W200, visible: true, type: ColumnTypeDTO.MONEY),
                new ColumnDTO(title: 'Type', attribute: DefinedTutorRole.CURRENT_PAYRATE + "." + PayRate.TYPE.name, sortable: false, width: W200, visible: true),
                new ColumnDTO(title: 'Oncosts %', attribute: DefinedTutorRole.CURRENT_PAYRATE + "." + PayRate.ONCOST_RATE.name, sortable: false, width: W200, visible: true),
        ]
        it.sortings = [
                new SortingDTO(attribute: DefinedTutorRole.NAME.name, ascending: true)
        ]
        it.layout = LayoutTypeDTO.THREE_COLUMN
        it.filterColumnWidth = W200
        it
    }

    private static final COURSECLASS_MODEL = new TableModelDTO().with {
        it.columns = [
                new ColumnDTO(title: 'Course', attribute: CourseClass.COURSE.dot(Course.NAME).name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Code', attribute: CourseClass.UNIQUE_CODE_PROPERTY, sortable: true, width: W200, visible: true,
                        sortFields: [CourseClass.COURSE.dot(Course.CODE).name,
                                     CourseClass.CODE.name]),
                new ColumnDTO(title: 'Starts', attribute: CourseClass.START_DATE_TIME.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.DATETIME),
                new ColumnDTO(title: 'Ends', attribute: CourseClass.END_DATE_TIME.name, sortable: true, width: W200, visible: true, type: ColumnTypeDTO.DATETIME),
                new ColumnDTO(title: 'Sessions', attribute: CourseClass.SESSIONS_COUNT.name, sortable: true, width: W200, visible: true),
                new ColumnDTO(title: 'Fee', attribute: CourseClass.FEE_INC_GST, sortable: false, width: W200, visible: true, type: ColumnTypeDTO.MONEY),
                new ColumnDTO(title: 'Tutor', attribute: CourseClass.TUTORS_ABRIDGED_PROP, sortable: false, width: W200, visible: true),
                new ColumnDTO(title: 'Site name', attribute: CourseClass.ROOM.dot(Room.SITE).dot(Site.NAME).name, sortable: true, width: W200, visible: true, sortFields: [CourseClass.ROOM.outer().dot(Room.SITE).outer().dot(Site.NAME).name]),
                new ColumnDTO(title: 'Enrolments', attribute: CourseClass.ENROLMENTS_COUNT_PROP, sortable: false, width: W200, visible: true),
                new ColumnDTO(title: 'Vacancies', attribute: CourseClass.PLACES_LEFT_PROP, sortable: false, width: W200, visible: true),
                new ColumnDTO(title: 'Cancelled', attribute: CourseClass.IS_CANCELLED.name, sortable: true, width: W200, visible: true, system: true,  type: ColumnTypeDTO.BOOLEAN),
                new ColumnDTO(title: 'Web visible', attribute: CourseClass.IS_SHOWN_ON_WEB.name, sortable: true, width: W200, visible: true, system: true,  type: ColumnTypeDTO.BOOLEAN),
                new ColumnDTO(title: 'Enabled', attribute: CourseClass.IS_ACTIVE.name, sortable: true, width: W200, visible: true, system: true, type: ColumnTypeDTO.BOOLEAN),
                new ColumnDTO(title: 'Self paced', attribute: CourseClass.IS_DISTANT_LEARNING_COURSE.name, sortable: true, width: W100, visible: false, system: true, type: ColumnTypeDTO.BOOLEAN),
                new ColumnDTO(title: 'Time zone', attribute: CourseClass.TIME_ZONE_ID, sortable: false, width: W200, visible: false, system: true)
        ]
        it.sortings = [
                new SortingDTO(attribute: CourseClass.START_DATE_TIME.name, ascending: false)
        ]
        it.layout = LayoutTypeDTO.THREE_COLUMN
        it.filterColumnWidth = W200
        it
    }

    private static final CONTACT_MODEL = new TableModelDTO().with {
        it.columns = [
                new ColumnDTO(title: 'Name', attribute: Contact.FULL_NAME_KEY, sortable: true,
                        width: W300, visible: true, sortFields: [Contact.LAST_NAME.name,
                                                                 Contact.FIRST_NAME.name,
                                                                 Contact.MIDDLE_NAME.name]),
                new ColumnDTO(title: 'Birthdate', attribute: Contact.BIRTH_DATE.name, sortable: true, type: ColumnTypeDTO.DATE,
                        width: W300, visible: true),
                new ColumnDTO(title: 'Street', attribute: Contact.STREET.name, sortable: true,
                        width: W200, visible: true),
                new ColumnDTO(title: 'Suburb', attribute: Contact.SUBURB.name, sortable: true,
                        width: W200, visible: true),
                new ColumnDTO(title: 'Student #', attribute: Contact.STUDENT.dot(Student.STUDENT_NUMBER).name, sortable: true,
                        width: W200, visible: true,
                        sortFields: [Contact.STUDENT.outer().dot(Student.STUDENT_NUMBER).name]
                ),
                new ColumnDTO(title: 'Type', attribute: Contact.CONTACT_TYPE_PROP, sortable: false,
                        width: W100, visible: true),
                new ColumnDTO(title: 'Tutor\'s date finished', attribute: Contact.TUTOR.dot(Tutor.DATE_FINISHED).name, sortable: true,
                        width: W100, visible: false, system: true, type: ColumnTypeDTO.DATETIME)
        ]
        it.layout = LayoutTypeDTO.THREE_COLUMN
        it.filterColumnWidth = W200
        it
    }

    // must be below model initializations
    public static final Map<String, TableModelDTO> DEFAULT_MODEL_MAP = [
            (Account.ENTITY_NAME)              : ACCOUNT_MODEL,
            (AccountTransaction.ENTITY_NAME)   : TRANSACTION_MODEL,
            (Audit.ENTITY_NAME)                : AUDIT_MODEL,
            (Banking.ENTITY_NAME)              : BANKING_MODEL,
            (CorporatePass.ENTITY_NAME)        : CORPORATE_PASS_MODEL,
            (Discount.ENTITY_NAME)             : DISCOUNT_MODEL,
            (Document.ENTITY_NAME)             : DOCUMENT_MODEL,
            (Invoice.ENTITY_NAME)              : INVOICE_MODEL,
            (Module.ENTITY_NAME)               : MODULE_MODEL,
            (PaymentIn.ENTITY_NAME)            : PAYMENT_IN_MODEL,
            (Payslip.ENTITY_NAME)              : PAYSLIP_MODEL,
            (Room.ENTITY_NAME)                 : ROOM_MODEL,
            (Script.ENTITY_NAME)               : SCRIPT_MODEL,
            (Site.ENTITY_NAME)                 : SITE_MODEL,
            (Qualification.ENTITY_NAME)        : QUAL_MODEL,
            (WaitingList.ENTITY_NAME)          : WAIT_LIST_MODEL,
            (Lead.ENTITY_NAME)                 : LEAD_MODEL,
            (Application.ENTITY_NAME)          : APPLICATION_MODEL,
            (ArticleProduct.ENTITY_NAME)       : ARTICLE_PRODUCT_MODEL,
            (Course.ENTITY_NAME)               : COURSE_MODEL,
            (VoucherProduct.ENTITY_NAME)       : VOUCHER_PRODUCT_MODEL,
            (MembershipProduct.ENTITY_NAME)    : MEMBERSHIP_PRODUCT_MODEL,
            (Certificate.ENTITY_NAME)          : CERTIFICATE_MODEL,
            (Survey.ENTITY_NAME)               : STUDENT_FEEDBACK_MODEL,
            (ProductItem.ENTITY_NAME)          : SALE_MODEL,
            (Outcome.ENTITY_NAME)              : OUTCOME_MODEL,
            (Assessment.ENTITY_NAME)           : ASSESSMENT_MODEL,
            (AssessmentSubmission.ENTITY_NAME) : ASSESSMENT_SUBMISSION_MODEL,
            (Enrolment.ENTITY_NAME)            : ENROLMENT_MODEL,
            (Message.ENTITY_NAME)              : MESSAGE_MODEL,
            (PaymentOut.ENTITY_NAME)           : PAYMENT_OUT_MODEL,
            (DefinedTutorRole.ENTITY_NAME)     : DEFINED_TUTOR_ROLE_MODEL,
            (CourseClass.ENTITY_NAME)          : COURSECLASS_MODEL,
            (Contact.ENTITY_NAME)              : CONTACT_MODEL,
            (PriorLearning.ENTITY_NAME)        : PRIOR_LEARNING_MODEL


    ] as Map<String, TableModelDTO>
}
