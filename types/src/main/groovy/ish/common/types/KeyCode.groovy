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
package ish.common.types

import ish.common.util.DisplayableExtendedEnumeration
import ish.oncourse.API

import static ish.common.types.Mask.*

/**
 * Keycode values are used for access rights. Each value specified is attached to an entity or a specific part of user interface.
 * Each KeyCode can have some or all of the following rights: View, Print, Create, Edit, Delete.
 *
 * Some Keycodes have rights which cannot be disabled and other rights which can never be enabled.
 */
@API
enum KeyCode implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 1
	 *
	 * Qualification, module and unit of competency reference data.
	 * Always enabled: view & print
	 * Always disabled: create & delete
	 */
	@API
	NTIS_DATA(1, "Qualification reference data", VIEW + PRINT),

	/**
	 * Database value: 2
	 *
	 * Relates to VET Statements of Attainment and Qualifications only. All contacts with class print permissions can create non-vocational certificates of attendance.
	 */
	@API
	CERTIFICATE(2, "Certificate"),

	/**
	 * Database value: 3
	 *
	 * Permission to work with all wait list records
	 */
	@API
	WAITING_LIST(3, "Waiting list"),

	/**
	 * Database value: 4
	 *
	 * Requires student edit and create. Enables quick enrol.
	 * Always disabled: delete
	 */
	@API
	ENROLMENT(4, "Enrolment", NONE, DELETE),

	/**
	 * Database value: 5
	 *
	 * Allow discount to be entered manually in Quick Enrol. Also allows negative charges' in QE.
	 */
	@API
	ENROLMENT_DISCOUNT(5, "Custom enrolment discount", ALL - VIEW), //

	/**
	 * Database value: 6
	 *
	 * Ability to add and modify sites.
	 * Always enabled: view
	 */
	@API
	SITE(6, "Site", VIEW),

	/**
	 * Database value: 7
	 *
	 * Ability add room to sites.
	 * Always enabled: view
	 */
	@API
	ROOM(7, "Room", VIEW),

	/**
	 * Database value: 8
	 *
	 * Permission to work with any course type, including traineeships, Vocational and non-vocational courses.
	 * Always enabled: view
	 */
	@API
	COURSE(8, "Course", VIEW),

	/**
	 * Database value: 9
	 *
	 * Restrict only VET tab on the course edit view.
	 * Always enabled: view
	 */
	@API
	VET_COURSE(9, "VET Course details", ALL - EDIT),

	/**
	 * Database value: 10
	 *
	 * Permission to work with any class type.
	 * Always enabled: view
	 */
	@API
	CLASS(10, "Class", VIEW),

	/**
	 * Database value: 11
	 *
	 * Restrict outcomes tab in class, enrolments tab in student, outcomes tab in enrolment, certificate window
	 * Always enabled: view
	 */
	@API
	OUTCOMES(11, "Enrolment outcomes", ALL - EDIT),

	/**
	 * Database value: 12
	 *
	 * Restrict access only to the budget tab within the class edit view
	 */
	@API
	BUDGET(12, "Budget"),

	/**
	 * Database value: 13
	 *
	 * Permission relates to sessions as they belong to classes.
	 * Always enabled: view
	 * Always disabled: print
	 */
	@API
	SESSION(13, "Session", VIEW, PRINT),

	/**
	 * Database value: 14
	 *
	 * This permission relates to the creation of discount strategies
	 */
	@API
	DISCOUNT(14, "Discount"),

	@Deprecated
	PROMO_CODE(15, "Promocode-obsolete", NONE, ALL),

	/**
	 * Database value: 16
	 *
	 * Rights to create, edit and delete Contacts (Students, Tutors, Companies). Required for working with enrolments.
	 * Always enabled: view
	 */
	@API
	CONTACT(16, "Contact", VIEW),

	/**
	 * Database value: 19
	 *
	 * Only the admin user is able to get to the access rights management views.
	 */
	@API
	ACL_ROLE(19, "Access", NONE, ALL),

	/**
	 * Database value: 20
	 *
	 * System Users are the users who log into onCourse or onCourse CMS.
	 * Always disabled: delete
	 */
	@API
	SYSTEMUSER(20, "Users", NONE, DELETE),

	@Deprecated
	PURCHASE(21, "Purchase", NONE, DELETE),

	/**
	 * Database value: 22
	 *
	 * This is the right to create a bare invoice (not part of Quick Enrol).
	 * Always disabled: delete
	 */
	@API
	INVOICE(22, "Invoice", NONE),

	/**
	 * Database value: 23
	 *
	 * Right to create a credit note (not part of cancelling class)
	 */
	@API
	INVOICE_CREDIT(23, "Credit note", ALL - CREATE),

	/**
	 * Database value: 24
	 *
	 * Permission relates only to manual payment in records, not those created during Quick Enrol.
	 */
	@API
	PAYMENT_IN(24, "Payment in", EDIT, DELETE),

	/**
	 * Database value: 25
	 *
	 * This permission is about creating refunds, usually processed in real time back to payer's credit cards.
	 * Always disabled: delete
	 */
	@API
	PAYMENT_OUT(25, "Payment out", EDIT, DELETE),

	/**
	 * Database value: 26
	 *
	 * Run the banking process, including marking payments as banked.
	 * Special single option.
	 */
	@API
	BANKING(26, "Banking", ALL - VIEW),

	/**
	 * Database value: 27
	 *
	 * Run the reconciliation process.
	 * Special single option.
	 */
	@API
	RECONCILIATION(27, "Reconciliation", ALL - VIEW),

	/**
	 * Database value: 28
	 *
	 * Account settings for onCourse chart of accounts
	 */
	@API
	ACCOUNT(28, "Account"),

	/**
	 * Database value: 29
	 *
	 * General ledger transaction records created during all financial transactions.
	 * Always disabled: delete & edit
	 */
	@API
	TRANSACTION(29, "Transaction", ALL - VIEW - EDIT - DELETE, DELETE | EDIT),

	/**
	 * Database value: 30
	 *
	 * The right to edit reports. Note that a user with this right could create a report which contains any other data from the whole database.
	 * Always enabled: view & print
	 */
	@API
	REPORT(30, "Report", VIEW + PRINT),

	/**
	 * Database value: 31
	 *
	 * Always enabled: view & print
	 */
	@API
	ATTACHMENT_INFO(31, "Documents", VIEW + PRINT, NONE, ALL),

	/**
	 * Database value: 32
	 *
	 * Permission relating to all tag groups, including those that drive the website navigation. This permission is not required to add tags to records, only to edit tag groups.
	 * Always enabled: view
	 */
	@API
	TAG(32, "Tag", VIEW + PRINT),

	@Deprecated
	WEBPAGE(33, "Web page", VIEW + PRINT),

	/**
	 * Database value: 34
	 *
	 * Permission to work with and add contacts to mailing list records.
	 * Always enabled: view
	 */
	@API
	MAILING_LIST(34, "Mailing list", VIEW + PRINT),

	/**
	 * Database value: 35
	 *
	 * Permission to modify available concessions. This permission is not needed to add concession types to contact records.
	 */
	@API
	CONCESSION_TYPE(35, "Concession type", NONE),

	/**
	 * Database value: 36
	 *
	 * This permission relates to the creation and editing of Products.
	 * Always enabled: view
	 */
	@API
	PRODUCT(36, "Product", VIEW),

	/**
	 * Database value: 37
	 */
	@API
	PAY_PERIOD(37, "Pay period", NONE, CREATE + DELETE),

	/**
	 * Database value: 38
	 *
	 * This permission relates to the creation and editing of payslips
	 */
	@API
	PAYSLIP(38, "Tutor pay", NONE, NONE),

	/**
	 * Database value: 39
	 *
	 * Determine pay rates for teaching staff.
	 * Always enabled: view
	 */
	@API
	TUTOR_ROLE(39, "Tutor roles"),

	/**
	 * Database value: 40
	 *
	 * Application function allows adding an approval process for the student requesting a place in a class
	 */
	@API
	APPLICATION(40, "Applications"),

	/**
	 * Database value: 41
	 *
	 * This permission relates to the creation and editing of Memberships.
	 * Always enabled: view
	 */
	@API
	MEMBERSHIP(41, "Memberships", VIEW),

	/**
	 * Database value: 42
	 *
	 * This permission relates to the creation and editing of Vouchers.
	 * Always enabled: view
	 */
	@API
	VOUCHER(42, "Vouchers", VIEW),

	/**
	 * Database value: 43
	 *
	 * Product sales.
	 * Always enabled: view
	 */
	@API
	SALE(43, "Sales", VIEW),

	/**
	 * Database value: 60
	 *
	 * he onCourse preferences that set the default accounts for various transaction types
	 */
	@API
	FINANCIAL_PREFERENCES(60, "Financial preferences", NONE, CREATE | PRINT | DELETE),

	/**
	 * Database value: 61
	 *
	 * Relates to onCourse application preferences that affect all users
	 */
	@API
	GENERAL_PREFERENCES(61, "General preferences", NONE, CREATE | PRINT | DELETE),

	/**
	 * Database value: 70
	 *
	 * Class duplication and rollover.
	 * Special single option.
	 */
	@API
	SPECIAL_DUPLICATE(70, "Class duplication/rollover", ALL - VIEW),

	/**
	 * Database value: 71
	 *
	 * Permission to cancel classes.
	 * Special single option.
	 */
	@API
	SPECIAL_CLASS_CANCEL(71, "Class cancellation", ALL - VIEW),

	/**
	 * Database value: 72
	 *
	 * Permission to perform export.
	 * Special single option.
	 */
	@API
	SPECIAL_EXPORT_XML(72, "Exporting to XML", ALL - VIEW),

	/**
	 * Database value: 73
	 *
	 * Create certificate from class.
	 * Special single option.
	 */
	@API
	SPECIAL_CERTIFICATE(73, "Creating certificate from class", ALL - VIEW),

	/**
	 * Database value: 74
	 *
	 * Send SMS to up to 50 contacts.
	 * Special single option.
	 */
	@API
	SPECIAL_SMS_50(74, "SMS up to 50 contacts", ALL - VIEW),

	/**
	 * Database value: 75
	 *
	 * Send email to up to 50 contacts.
	 * Special single option.
	 */
	@API
	SPECIAL_EMAIL_50(75, "Email up to 50 contacts", ALL - VIEW),

	/**
	 * Database value: 76
	 *
	 * Send SMS to over 50 contacts.
	 * Special single option.
	 */
	@API
	SPECIAL_SMS_MASS(76, "SMS over 50 contacts", ALL - VIEW),

	/**
	 * Database value: 77
	 *
	 * Send email to over 50 contacts.
	 * Special single option.
	 */
	@API
	SPECIAL_EMAIL_MASS(77, "Email over 50 contacts", ALL - VIEW),

	/**
	 * Database value: 78
	 *
	 * Merge two contacts.
	 * Special single option.
	 */
	@API
	SPECIAL_DE_DUPE(78, "Contact merging", ALL - VIEW),

	/**
	 * Database value: 79
	 *
	 * Cancel and transfer enrolments
	 * Special single option.
	 */
	@API
	SPECIAL_CANCEL_TRANSFER_ENROLMENTS(79, "Enrolment cancellation and transferring", ALL - VIEW),

	/**
	 * Database value: 80
	 *
	 * Export training data for government reporting.
	 * Special single option.
	 */
	@API
	SPECIAL_AVETMISS_EXPORT(80, "Export AVETMISS", ALL - VIEW),

	/**
	 * Database value: 81
	 *
	 * Import date. Note that this gives the user the ability to create records they may not otherwise be able to create with their existing rights.
	 * Special single option.
	 */
	@API
	SPECIAL_IMPORT(81, "Data import", ALL - VIEW),

	@Deprecated
	SPECIAL_DET_EXPORT(82, "Export DET AVETMISS", ALL - VIEW),

	/**
	 * Database value: 83
	 *
	 * Permission to specify custom administration centre.
	 * Special single option.
	 */
	@API
	SPECIAL_CHANGE_ADMINISTRATION_CENTRE(83, "Change administration centre", ALL - VIEW),

	/**
	 * Database value: 84
	 *
	 * Ability to override tutor payrates in class budget.
	 * Special single option.
	 */
	@API
	SPECIAL_OVERRIDE_TUTOR_PAYRATE(84, "Override tutor pay rate"),

	/**
	 * Database value: 85
	 *
	 * Permission to modify Export Templates.
	 * Always enabled: view
	 */
	@API
	EXPORT_TEMPLATE(85, "ExportTemplate", VIEW),

	/**
	 * Database value: 86
	 *
	 * Setting up days (e.g. holidays) that will be marked unavailable in class timetable
	 */
	@API
	UNAVAILABLE_RULE(86, "UnavailableRule", ALL),

	/**
	 * Database value: 87
	 *
	 * Permission to modify Email Templates.
	 * Always enabled: view
	 */
	@API
	EMAIL_TEMPLATE(87, "EmailTemplate", VIEW),

	/**
	 * Database value: 88
	 *
	 * Permissions relating to the creation or editing of CorporatePass.
	 */
	@API
	CORPORATE_PASS(88, "CorporatePass"),

	/**
	 * Database value: 89
	 *
	 * Permission that allows a user to export/print MYOB Export from the Financial menu.
	 * Special single option.
	 */
	@API
	SPECIAL_MYOB_EXPORT(89, "Export to MYOB", ALL - VIEW),

	/**
	 * Database value: 90
	 *
	 * Permission that allows a user to export/print Trial Balance from the Financial menu.
	 * Special single option.
	 */
	@API
    SPECIAL_TRIAL_BALANCE(90, "Trial balance", ALL - VIEW),

	/**
	 * Database value: 91
	 *
	 * Ability to print certificates without a USI verified for that student
	 */
	@API
	PRINT_CERTIFICATE_WITHOUT_VERIFIED_USI(91, "Print certificate without verified USI", ALL - VIEW),

	/**
	 * Database value: 92
	 *
	 * Ability to print certificates without a USI entered for that student
	 */
	@API
	PRINT_CERTIFICATE_WITHOUT_USI(92, "Print certificate without USI", ALL - VIEW),

	/**
	 * Database value: 93
	 *
	 * Permission to modify scripts.
	 * Always enabled: view
	 */
	@API
	SCRIPT_TEMPLATE(93, "Scripts", VIEW),

	/**
	 * Database value: 94
	 *
	 * Ability to create and edit relation types that can be used to link contacts (e.g. Parent - Child)
	 * Always enabled: view
	 */
	@API
	CONTACT_RELATION_TYPE(94, "Contact relation types", VIEW),

	/**
	 * Database value: 95
	 *
	 * Ability to create payment plan lines for invoice
	 * Always disabled
	 */
	@API
	PAYMENT_PLAN(95, "Payment plan"),

	/**
	 * Database value: 96
	 *
	 * Ability to edit notes
	 * Special single option.
	 */
	@API
	SPECIAL_EDIT_NOTES(96, "Edit/Delete Notes", ALL - VIEW),

	/**
	 * Database value: 97
	 *
	 * Add additional two factor authentication mechanism to login. If this is enabled then a user who logs in without two factor authentication enabled is immediately shown the "Enable two factor authentication" dialog.
	 * Special single option.
	 */
	@API
	SPECIAL_TWO_FACTOR_AUTHENTICATION(97, "Require two factor authentication", ALL - VIEW),

	/**
	 * Database value: 98
	 *
	 * Ability to create payment methods for payments.
	 */
	@API
	PAYMENT_METHOD(98, "Payment Method"),

	/**
	 * Database value: 100
	 *
	 * Ability to edit quality rules
	 */
	@API
	QUALITY_RULE(100, "Quality rule"),

	/**
	 * Database value: 101
	 *
	 * Allow to access "Summary extracts" item in financial menu
	 * Special single option.
	 */
	@API
	SUMMARY_EXTRACTS(101, "Summary extracts", ALL - VIEW),

	/**
	 * Database value: 102
	 *
	 * Allow to access "Audit logging"
	 * Special single option.
	 */
	@API
	AUDIT_LOGGING(102, "Audit logging", NONE, CREATE | EDIT | DELETE),


	/**
	 * Database value: 103
	 *
	 * Allow to access "Funding contract"
	 */
	@API
	FUNDING_CONTRACT(103, "Funding contract"),

	/**
	 * Database value: 104
	 *
	 * Allow to access "Funding upload"
	 */
	@API
	FUNDING_UPLOAD(104, "Funding upload"),

	/**
	 * Database value: 105
	 *
	 * Allow to access private documents
	 */
	@API
	PRIVATE_DOCUMENTS(105, "Private documents", NONE, PRINT + DELETE, VIEW + EDIT + CREATE),

    /**
     * Database value: 106
     *
     * Allow to access "Student feedback"
     */
    @API
    SURVEYS(106, "Student feedback", VIEW, CREATE + DELETE,  EDIT + PRINT),

    /**
     * Database value: 107
     *
     * Allow to edit the payable time at the per tutor per session level in the class attendance tab
     * Special single option.
     */
    @API
    OVERRIDE_TUTOR_SESSION_PAYABLE_TIME(107, "Override tutor session payable time"),

    /**
     * Database value: 108
     *
     * Allow to click the 'confirm now' button in the Generate tutor payroll sheet that confirms all the unconfirmed paylines
     * Special single option.
     */
    @API
    BULK_CONFIRM_TUTOR_WAGES(108, "Bulk confirm tutor wages"),

    /**
     * Database value: 109
     *
     * Relates to onCourse application preferences that affect all users and open in java embedded web browser
     */
    @API
    GENERAL_PREFERENCES_HTML(109, "HTML general preferences", NONE, CREATE | PRINT | DELETE),

	/**
     * Database value: 110
     *
     * Permission to work with any assessment.
     */
	@API
    ASSESSMENT(110, "Assessment", VIEW, NONE, ALL)


	private int value
	private String displayName
	private int alwaysAllowedMask
	private int neverAllowedMask
	private int defaultValueMask

	private KeyCode(int value, String displayName) {
		this(value, displayName, NONE, NONE, NONE)
	}

	/**
	 * @param value as stored in the database
	 * @param alwaysAllowedMask bits in this mask are always set as allowed (that is, adding Mask.VIEW here means this key always allows access for view)
	 */
	private KeyCode(int value, String displayName, int alwaysAllowedMask) {
		this(value, displayName, alwaysAllowedMask, NONE, NONE)
	}

	/**
	 * @param value as stored in the database
	 * @param alwaysAllowedMask bits in this mask are always set as allowed (that is, adding Mask.VIEW here means this key always allows access for view)
	 * @param neverAllowedMask bits in this mask are never allowed
	 */
	private KeyCode(int value, String displayName, int alwaysAllowedMask, int neverAllowedMask) {
		this(value, displayName, alwaysAllowedMask, neverAllowedMask, NONE)
	}

	/**
	 * @param value as stored in the database
	 * @param alwaysAllowedMask bits in this mask are always set as allowed (that is, adding Mask.VIEW here means this key always allows access for view)
	 * @param neverAllowedMask bits in this mask are never allowed
	 * @param defaultValueMask bits in this mask are default values
	 */
	private KeyCode(int value, String displayName, int alwaysAllowedMask, int neverAllowedMask, int defaultValueMask) {
		this.value = value
		this.alwaysAllowedMask = alwaysAllowedMask
		this.neverAllowedMask = neverAllowedMask
		this.displayName = displayName
		this.defaultValueMask = defaultValueMask
	}


	/**
	 * @see ish.common.util.DisplayableExtendedEnumeration#getDatabaseValue()
	 */
	@Override
	Integer getDatabaseValue() {
		return value
	}

	/**
	 * This mask are the rights always allowed for this key for all users
	 *
	 * @return the alwaysAllowedMask
	 */
	int getAlwaysAllowedMask() {
		return alwaysAllowedMask
	}

	/**
	 * The mask for rights never allowed for any user.
	 *
	 * @return the neverAllowedMask
	 */
	int getNeverAllowedMask() {
		return neverAllowedMask
	}

	/**
	 * The mask for default rights if they haven't created yet.
	 *
	 * @return the defaultValueMask
	 */
	int getDefaultValueMask() {
		return defaultValueMask
	}

	/**
	 * The name for this keycode as it should be seen by the end user
	 *
	 * @return the displayName
	 */
	@Override
	String getDisplayName() {
		return displayName
	}

}
