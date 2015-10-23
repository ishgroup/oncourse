/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.API;

/**
 * Keycode values are used for access rights. Each value specified is attached to an entity or a specific part of user interface.
 * Each KeyCode can have some or all of the following rights:
 * <ul>
 *     <li>View</li>
 *     <li>Print</li>
 *     <li>Create</li>
 *     <li>Edit</li>
 *     <li>Delete</li>
 * </ul>
 * 
 * Some Keycodes have rights which cannot be disabled and other rights which can never be enabled. 
 * 
 */
@API
public enum KeyCode implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Qualification, module and unit of competency reference data.
	 * Always enabled: view & print
	 * Always disabled: create & delete
	 * 
	 * Database value: 1
	 */
	@API
	NTIS_DATA(1, "Qualification reference data", Mask.VIEW + Mask.PRINT, Mask.CREATE + Mask.DELETE),

	/**
	 * Database value: 2
	 */
	@API
	CERTIFICATE(2, "Certificate"),

	/**
	 * Database value: 3
	 */
	@API
	WAITING_LIST(3, "Waiting list"),

	/**
	 * Requires student edit and create. Enables quick enrol.
	 * Always disabled: delete
	 * 
	 * Database value: 4
	 */
	@API
	ENROLMENT(4, "Enrolment", Mask.NONE, Mask.DELETE),

	/**
	 * Allow discount to be entered manually in Quick Enrol. Also allows negative charges' in QE.
	 * Only appli
	 * 
	 * Database value: 5
	 */
	@API
	ENROLMENT_DISCOUNT(5, "Custom enrolment discount", Mask.ALL - Mask.VIEW), // 

	/**
	 * Always enabled: view
	 * 
	 * Database value: 6
	 */
	@API
	SITE(6, "Site", Mask.VIEW),

	/**
	 * Always enabled: view
	 * 
	 * Database value: 7
	 */
	@API
	ROOM(7, "Room", Mask.VIEW),

	/**
	 * Always enabled: view
	 * 
	 * Database value: 8
	 */
	@API
	COURSE(8, "Course", Mask.VIEW),
	
	/**
	 * Restrict only VET tab on the course edit view.
	 * Always enabled: view
	 * 
	 * Database value: 9
	 */
	@API
	VET_COURSE(9, "VET Course details", Mask.ALL - Mask.EDIT),

	/**
	 * Always enabled: view
	 * 
	 * Database value: 10
	 */
	@API
	CLASS(10, "Class", Mask.VIEW),

	/**
	 * Restrict outcomes tab in class, enrolments tab in student, outcomes tab in enrolment, certificate window
	 * Always enabled: view 
	 * 
	 * Database value: 11
	 */
	@API
	OUTCOMES(11, "Enrolment outcomes", Mask.ALL - Mask.EDIT),

	/**
	 * Restrict access only to the budget tab within the class edit view
	 * 
	 * Database value: 12
	 */
	@API
	BUDGET(12, "Budget"),

	/**
	 * Always enabled: view
	 * Always disabled: print
	 * 
	 * Database value: 13
	 */
	@API
	SESSION(13, "Session", Mask.VIEW, Mask.PRINT),

	/**
	 * Database value: 14
	 */
	@API
	DISCOUNT(14, "Discount"),
	
	@Deprecated
	PROMO_CODE(15, "Promocode-obsolete", Mask.NONE, Mask.ALL),

	/**
	 * Always enabled: view
	 * 
	 * Database value: 16
	 */
	@API
	CONTACT(16, "Contact", Mask.VIEW),

	/**
	 * Only the admin user is able to get to the access rights management views.
	 * 
	 * Database value: 19
	 */
	@API
	ACL_ROLE(19, "Access", Mask.NONE, Mask.ALL),

	/**
	 * System Users are the users who log into onCourse or onCourse CMS.
	 * Always disabled: delete
	 *
	 * Database value: 20
	 */
	@API
	SYSTEMUSER(20, "Users", Mask.NONE, Mask.DELETE),
	
	@Deprecated
	PURCHASE(21, "Purchase", Mask.NONE, Mask.DELETE),

	/**
	 * This is the right to create a bare invoice (not part of Quick Enrol)
	 * Always disabled: delete
	 * 
	 * Database value: 22
	 */
	@API
	INVOICE(22, "Invoice", Mask.NONE, Mask.DELETE),

	/**
	 * Right to create a credit note (not part of cancelling class)
	 * 
	 * Database value: 23
	 */
	@API
	INVOICE_CREDIT(23, "Credit note", Mask.ALL - Mask.CREATE),

	/**
	 * Database value: 24
	 */
	@API
	PAYMENT_IN(24, "Payment in", Mask.EDIT, Mask.DELETE),

	/**
	 * Always disabled: delete
	 * 
	 * Database value: 25
	 */
	@API
	PAYMENT_OUT(25, "Payment out", Mask.EDIT, Mask.DELETE),

	/**
	 * Run the banking process, including marking payments as banked.
	 * Special single option.
	 * 
	 * Database value: 26
	 */
	@API
	BANKING(26, "Banking", Mask.ALL - Mask.VIEW),

	/**
	 * Run the reconciliation process.
	 * Special single option.
	 *
	 * Database value: 27
	 */
	@API
	RECONCILIATION(27, "Reconciliation", Mask.ALL - Mask.VIEW),

	/**
	 * Database value: 28
	 */
	@API
	ACCOUNT(28, "Account"),

	/**
	 * Always disabled: delete & edit
	 * 
	 * Database value: 29
	 */
	@API
	TRANSACTION(29, "Transaction", Mask.ALL - Mask.VIEW - Mask.EDIT - Mask.DELETE, Mask.DELETE | Mask.EDIT),

	/**
	 * The right to edit reports. Note that a user with this right could create a report which contains any other data from the whole database.
	 * Always enabled: view & print
	 * 
	 * Database value: 30
	 */
	@API
	REPORT(30, "Report", Mask.VIEW + Mask.PRINT),

	/**
	 * Always enabled: view & print
	 * 
	 * Database value: 31
	 */
	@API
	ATTACHMENT_INFO(31, "Documents", Mask.VIEW + Mask.PRINT),

	/**
	 * Always enabled: view
	 * 
	 * Database value: 32
	 */
	@API
	TAG(32, "Tag", Mask.VIEW + Mask.PRINT),

	@Deprecated
	WEBPAGE(33, "Web page", Mask.VIEW + Mask.PRINT),

	/**
	 * Always enabled: view
	 * 
	 * Database value: 34
	 */
	@API
	MAILING_LIST(34, "Mailing list", Mask.VIEW + Mask.PRINT),

	/**
	 * Database value: 35
	 */
	@API
	CONCESSION_TYPE(35, "Concession type", Mask.NONE),
	
	/**
	 * Always enabled: view
	 * 
	 * Database value: 36
	 */
	@API
	PRODUCT(36, "Product", Mask.VIEW),

	/**
	 * Database value: 37
	 */
	@API
	PAY_PERIOD(37, "Pay period", Mask.NONE, Mask.CREATE + Mask.DELETE),

	/**
	 * Database value: 38
	 */
	@API
	PAYSLIP(38, "Tutor pay", Mask.NONE, Mask.NONE),

	/**
	 * Always enabled: view
	 * 
	 * Database value: 39
	 */
	@API
	TUTOR_ROLE(39, "Tutor roles"),

	/**
	 * Database value: 40
	 */
	@API
	APPLICATION(40, "Applications"),

	/**
	 * Always enabled: view
	 * 
	 * Database value: 41
	 */
	@API
	MEMBERSHIP(41, "Memberships", Mask.VIEW),
	
	/**
	 * Always enabled: view
	 * 
	 * Database value: 42
	 */
	@API
	VOUCHER(42, "Vouchers", Mask.VIEW),
	
	/**
	 * Product sales.
	 * Always enabled: view
	 * 
	 * Database value: 43
	 */
	@API
	SALE(43, "Sales", Mask.VIEW),
	
	/**
	 * Database value: 60
	 */
	@API
	FINANCIAL_PREFERENCES(60, "Financial preferences", Mask.NONE, Mask.CREATE | Mask.PRINT | Mask.DELETE),

	/**
	 * Database value: 61
	 */
	@API
	GENERAL_PREFERENCES(61, "General preferences", Mask.NONE, Mask.CREATE | Mask.PRINT | Mask.DELETE),

	/**
	 * Class duplication and rollover.
	 * Special single option.
	 * 
	 * Database value: 70
	 */
	@API
	SPECIAL_DUPLICATE(70, "Class duplication/rollover", Mask.ALL - Mask.VIEW),

	/**
	 * Class cancellation.
	 * Special single option.
	 *
	 * Database value: 71
	 */
	@API
	SPECIAL_CLASS_CANCEL(71, "Class cancellation", Mask.ALL - Mask.VIEW),

	/**
	 * Export.
	 * Special single option.
	 *
	 * Database value: 72
	 */
	@API
	SPECIAL_EXPORT_XML(72, "Exporting to XML", Mask.ALL - Mask.VIEW),

	/**
	 * Create certificate from class.
	 * Special single option.
	 *
	 * Database value: 73
	 */
	@API
	SPECIAL_CERTIFICATE(73, "Creating certificate from class", Mask.ALL - Mask.VIEW),

	/**
	 * Send SMS to up to 50 contacts.
	 * Special single option.
	 *
	 * Database value: 74
	 */
	@API
	SPECIAL_SMS_50(74, "SMS up to 50 contacts", Mask.ALL - Mask.VIEW),

	/**
	 * Send email to up to 50 contacts.
	 * Special single option.
	 *
	 * Database value: 75
	 */
	@API
	SPECIAL_EMAIL_50(75, "Email up to 50 contacts", Mask.ALL - Mask.VIEW),

	/**
	 * Send SMS to over 50 contacts.
	 * Special single option.
	 *
	 * Database value: 76
	 */
	@API
	SPECIAL_SMS_MASS(76, "SMS over 50 contacts", Mask.ALL - Mask.VIEW),

	/**
	 * Send email to over 50 contacts.
	 * Special single option.
	 *
	 * Database value: 77
	 */
	@API
	SPECIAL_EMAIL_MASS(77, "Email over 50 contacts", Mask.ALL - Mask.VIEW),

	/**
	 * Merge two contacts.
	 * Special single option.
	 *
	 * Database value: 78
	 */
	@API
	SPECIAL_DE_DUPE(78, "Contact merging", Mask.ALL - Mask.VIEW),

	/**
	 * Cancel enrolments
	 * Special single option.
	 *
	 * Database value: 79
	 */
	@API
	SPECIAL_CANCEL_TRANSFER_ENROLMENTS(79, "Enrolment cancellation and transferring", Mask.ALL - Mask.VIEW),

	/**
	 * Export AVETMISS.
	 * Special single option.
	 *
	 * Database value: 80
	 */
	@API
	SPECIAL_AVETMISS_EXPORT(80, "Export AVETMISS", Mask.ALL - Mask.VIEW),

	/**
	 * Import date. Note that this gives the user the ability to create records they may not otherwise be able to create with their existing rights.
	 * Special single option.
	 *
	 * Database value: 81
	 */
	@API
	SPECIAL_IMPORT(81, "Data import", Mask.ALL - Mask.VIEW),

	@Deprecated
	SPECIAL_DET_EXPORT(82, "Export DET AVETMISS", Mask.ALL - Mask.VIEW),

	/**
	 * Special single option.
	 * 
	 * Database value: 83
	 */
	@API
	SPECIAL_CHANGE_ADMINISTRATION_CENTRE(83, "Change administration centre", Mask.ALL - Mask.VIEW),

	/**
	 * Special single option.
	 * 
	 * Database value: 84
	 */
	@API
	SPECIAL_OVERRIDE_TUTOR_PAYRATE(84, "Override tutor"),

	/**
	 * Always enabled: view
	 * 
	 * Database value: 85
	 */
	@API
	EXPORT_TEMPLATE(85, "ExportTemplate", Mask.VIEW),

	/**
	 * Database value: 86
	 */
	@API
	UNAVAILABLE_RULE(86, "UnavailableRule", Mask.ALL), // TODO: really, who can do what?
	
	/**
	 * Always enabled: view
	 * 
	 * Database value: 87
	 */
	@API
	EMAIL_TEMPLATE(87, "EmailTemplate", Mask.VIEW),

	/**
	 * Database value: 88
	 */
	@API
	CORPORATE_PASS(88, "CorporatePass"),

	/**
	 * Allow the export to MYOB feature.
	 * Special single option.
	 * 
	 * Database value: 89
	 */
	@API
	SPECIAL_MYOB_EXPORT(89, "Export to MYOB", Mask.ALL - Mask.VIEW),
	
	/**
	 * Special single option.
	 * 
	 * Database value: 90
	 */
	@API
    SPECIAL_TRIAL_BALANCE(90, "Trial balance", Mask.ALL - Mask.VIEW),

	/**
	 * Ability to print certificates without a USI <b>verified</b> for that student
	 *
	 * Database value: 91
	 */
	@API
	PRINT_CERTIFICATE_WITHOUT_VERIFIED_USI(91, "Print certificate without verified USI", Mask.ALL - Mask.VIEW),

	/**
	 * Ability to print certificates without a USI <b>entered</b> for that student
	 *
	 * Database value: 92
	 */
	@API
	PRINT_CERTIFICATE_WITHOUT_USI(92, "Print certificate without USI", Mask.ALL - Mask.VIEW),

	/**
	 * Always enabled: view
	 *
	 * Database value: 93
	 */
	@API
	SCRIPT_TEMPLATE(93, "Scripts", Mask.VIEW),

	/**
	 * Always enabled: view
	 *
	 * Database value: 94
	 */
	@API
	CONTACT_RELATION_TYPE(94, "Contact relation types", Mask.VIEW),

	/**
	 * Ability to create payment plan lines for invoice
	 * Always disabled
	 * 
	 * Database value: 95
	 */
	@API
	PAYMENT_PLAN(95, "Payment plan"),

	/**
	 * Ability to edit notes
	 * Special single option.
	 *
	 * Database value: 96
	 */
	@API
	SPECIAL_EDIT_NOTES(96, "Edit/Delete Notes", Mask.ALL - Mask.VIEW),

	/**
	 * Special single option.
	 *
	 * Database value: 97
	 */
	@API
	SPECIAL_TWO_FACTOR_AUTHENTICATION(97, "Require two factor authentication", Mask.ALL - Mask.VIEW),

	/**
	 * Ability to create payment methods for payments
	 * 
	 * Database value: 98
	 */
	@API
	PAYMENT_METHOD(98, "Payment Method"),

	/**
	 * Ability to view messages
	 *
	 * Database value: 99
	 */
	@API
	MESSAGE(99, "Message"),

	/**
	 * Ability to edit quality rules
	 *
	 * Database value: 100
	 */
	@API
	QUALITY_RULE(100, "Quality rule");
	

	private int value;
	private String displayName;
	private int alwaysAllowedMask;
	private int neverAllowedMask;

	private KeyCode(int value, String displayName) {
		this(value, displayName, Mask.NONE, Mask.NONE);
	}

	/**
	 * @param value as stored in the database
	 * @param alwaysAllowedMask bits in this mask are always set as allowed (that is, adding Mask.VIEW here means this key always allows access for view)
	 */
	private KeyCode(int value, String displayName, int alwaysAllowedMask) {
		this(value, displayName, alwaysAllowedMask, Mask.NONE);
	}

	/**
	 * @param value as stored in the database
	 * @param alwaysAllowedMask bits in this mask are always set as allowed (that is, adding Mask.VIEW here means this key always allows access for view)
	 * @param neverAllowedMask bits in this mask are never allowed
	 */
	private KeyCode(int value, String displayName, int alwaysAllowedMask, int neverAllowedMask) {
		this.value = value;
		this.alwaysAllowedMask = alwaysAllowedMask;
		this.neverAllowedMask = neverAllowedMask;
		this.displayName = displayName;
	}

	/**
	 * @see ish.common.util.DisplayableExtendedEnumeration#getDatabaseValue()
	 */
	@Override
	public Integer getDatabaseValue() {
		return this.value;
	}

	/**
	 * This mask are the rights always allowed for this key for all users
	 * 
	 * @return the alwaysAllowedMask
	 */
	public int getAlwaysAllowedMask() {
		return this.alwaysAllowedMask;
	}

	/**
	 * The mask for rights never allowed for any user.
	 * 
	 * @return the neverAllowedMask
	 */
	public int getNeverAllowedMask() {
		return this.neverAllowedMask;
	}

	/**
	 * The name for this keycode as it should be seen by the end user
	 * 
	 * @return the displayName
	 */
	@Override
	public String getDisplayName() {
		return this.displayName;
	}

}
