/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * collection of keycode values,used for access rights. Each value specified is attached to an entity or a specific part of user interface
 * 
 * @author marcin
 */
public enum KeyCode implements DisplayableExtendedEnumeration<Integer> {

	NTIS_DATA(1, "Qualification reference data", Mask.VIEW + Mask.PRINT, Mask.CREATE + Mask.DELETE), // create and delete not available

	CERTIFICATE(2, "Certificate"),

	WAITING_LIST(3, "Waiting list"),

	ENROLMENT(4, "Enrolment", Mask.NONE, Mask.DELETE),
	// requires: students edit, create
	// allows: single shot email confirmation, create invoice/payment specifically within QE

	ENROLMENT_DISCOUNT(5, "Custom enrolment discount", Mask.ALL - Mask.VIEW), // allow discount to be entered manually on QE. Also allows negative
	// charges' in QE.

	SITE(6, "Site", Mask.VIEW), // view always permitted
	ROOM(7, "Room", Mask.VIEW), // view always permitted

	COURSE(8, "Course", Mask.VIEW), // view always permitted
	VET_COURSE(9, "VET Course details", Mask.ALL - Mask.EDIT), // restrict only VET tab, everyone can see, not all can edit

	CLASS(10, "Class", Mask.VIEW), // view always permitted
	OUTCOMES(11, "Enrolment outcomes", Mask.ALL - Mask.EDIT), // restrict outcomes tab in class, enrolments tab in student, outcomes tab in enrolment,
	// certificate window
	BUDGET(12, "Budget"), // restrict budget tab

	SESSION(13, "Session", Mask.VIEW, Mask.PRINT), // view always permitted

	DISCOUNT(14, "Discount"),
	PROMO_CODE(15, "Promocode-obsolete", Mask.NONE, Mask.ALL), // obsolete, nothing allowed anymore.

	CONTACT(16, "Contact", Mask.VIEW), // view always permitted

	ACL_ROLE(19, "Access", Mask.NONE, Mask.ALL), // only accessible by admin user
	SYSTEMUSER(20, "Users", Mask.NONE, Mask.DELETE),

	PURCHASE(21, "Purchase", Mask.NONE, Mask.DELETE),
	INVOICE(22, "Invoice", Mask.NONE, Mask.DELETE), // right to create a bare invoice (not part of QE)
	INVOICE_CREDIT(23, "Credit note", Mask.ALL - Mask.CREATE), // right to create a credit note (not part of cancelling class)
	PAYMENT_IN(24, "Payment in", Mask.EDIT, Mask.DELETE),
	PAYMENT_OUT(25, "Payment out", Mask.EDIT, Mask.DELETE),
	BANKING(26, "Banking", Mask.ALL - Mask.VIEW),
	RECONCILIATION(27, "Reconciliation", Mask.ALL - Mask.VIEW),

	ACCOUNT(28, "Account"),
	TRANSACTION(29, "Transaction", Mask.ALL - Mask.VIEW - Mask.EDIT - Mask.DELETE, Mask.DELETE | Mask.EDIT),
	REPORT(30, "Report", Mask.VIEW), // view always permitted

	ATTACHMENT_INFO(31, "Attachment", Mask.VIEW + Mask.PRINT), // view and print always permitted
	TAG(32, "Tag", Mask.VIEW + Mask.PRINT), // view always permitted
	WEBPAGE(33, "Web page", Mask.VIEW + Mask.PRINT), // view always permitted
	MAILING_LIST(34, "Mailing list", Mask.VIEW + Mask.PRINT), // view always permitted

	CONCESSION_TYPE(35, "Concession type", Mask.NONE),

	PRODUCT(36, "Product", Mask.VIEW), // view always permitted

	PAY_PERIOD(37, "Pay period", Mask.NONE, Mask.CREATE + Mask.DELETE),
	PAYSLIP(38, "Tutor pay", Mask.NONE, Mask.NONE),

	TUTOR_ROLE(39, "Tutor roles"), // view always permitted

	APPLICATION(40, "Applications"),
	
	FINANCIAL_PREFERENCES(60, "Financial preferences", Mask.NONE, Mask.CREATE | Mask.PRINT | Mask.DELETE),
	GENERAL_PREFERENCES(61, "General preferences", Mask.NONE, Mask.CREATE | Mask.PRINT | Mask.DELETE),

	// these next few only have the view right (all other bits are masked out)
	SPECIAL_DUPLICATE(70, "Class duplication/rollover", Mask.ALL - Mask.VIEW),
	SPECIAL_CLASS_CANCEL(71, "Class cancellation", Mask.ALL - Mask.VIEW),
	SPECIAL_EXPORT_XML(72, "Exporting to InDesign", Mask.ALL - Mask.VIEW),
	SPECIAL_CERTIFICATE(73, "Creating certificate from class", Mask.ALL - Mask.VIEW),
	SPECIAL_SMS_50(74, "SMS up to 50 contacts", Mask.ALL - Mask.VIEW),
	SPECIAL_EMAIL_50(75, "Email up to 50 contacts", Mask.ALL - Mask.VIEW),
	SPECIAL_SMS_MASS(76, "SMS over 50 contacts", Mask.ALL - Mask.VIEW),
	SPECIAL_EMAIL_MASS(77, "Email over 50 contacts", Mask.ALL - Mask.VIEW),
	SPECIAL_DE_DUPE(78, "Contact merging", Mask.ALL - Mask.VIEW),
	SPECIAL_CANCEL_ENROLMENTS(79, "Enrolment cancellation", Mask.ALL - Mask.VIEW),
	SPECIAL_AVETMISS_EXPORT(80, "Export AVETMISS", Mask.ALL - Mask.VIEW),
	SPECIAL_IMPORT(81, "Data import", Mask.ALL - Mask.VIEW),
	SPECIAL_DET_EXPORT(82, "Export DET AVETMISS", Mask.ALL - Mask.VIEW),
	SPECIAL_CHANGE_ADMINISTRATION_CENTRE(83, "Change administration centre", Mask.ALL - Mask.VIEW),
	SPECIAL_OVERRIDE_TUTOR_PAYRATE(84, "Override tutor payrate", Mask.ALL - Mask.EDIT),

	EXPORTTEMPLATE(85, "ExportTemplate", Mask.VIEW), // view always permitted

	UNAVAILABLE_RULE(86, "UnavailableRule", Mask.ALL), // TODO: really, who can do what?
	MESSAGE_TEMPLATE(87, "EmailTemplate", Mask.VIEW),

	CORPORATE_PASS(88, "CorporatePass"),

	SPECIAL_MYOB_EXPORT(89, "Export to MYOB", Mask.ALL - Mask.VIEW),
    SPECIAL_TRIAL_BALANCE(90, "Trial balance", Mask.ALL - Mask.VIEW);

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
