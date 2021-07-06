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

package ish.persistence;

import ish.oncourse.API;

/**
 * List of all preferences which can be accessed in the system. You can access the values most easily as direct attributes:
 * <code>
 *     preference.college.name
 * </code>
 *
 * but you can also use the string key:
 * <code>
 *     Preference.valueString("college.name")
 * </code>
 */
@API
public interface Preferences {

	// **************************************
	// College information
	// **************************************

	/**
	 * College name
	 *
	 * @param prefKey college.name
	 */
	@API
	String COLLEGE_NAME = "college.name";

	/**
	 * College ABN
	 *
	 * @param prefKey college.abn
	 */
	@API
	String COLLEGE_ABN = "college.abn";

	/**
	 * College website URL. If you have multiple sites, use the primary one here since this will be used for references
	 * to street directions and other information in an enrolment confirmation.
	 *
	 * @param prefKey web.url
	 */
	@API
	String COLLEGE_URL = "web.url";

	/**
	 * Invoice remittance instructions. This additional text will be added to every invoice.
	 *
	 * @param prefKey college.paymentInfo
	 */
	@API
	String COLLEGE_PAYMENT_INFO = "college.paymentInfo";

	String COLLEGE_ENROL_SUCCESS_URL = "enrol.postSuccessURL";

	String PAYMENT_GATEWAY_PASS = "payment.gateway.api.key";

	String PAYMENT_GATEWAY_PURCHASE_WITHOUT_AUTH = "payment.gateway.purchase-without-auth";



	// **************************************
	// Services
	// **************************************

	/**
	 * Default server time zone. Don't change this without conisidering how the change affects your data.
	 *
	 * @param prefKey oncourse.server.timezone.default
	 */
	@API
	String ONCOURSE_SERVER_DEFAULT_TZ = "oncourse.server.timezone.default";

	String SERVICES_LDAP_AUTHENTICATION = "services.ldap.authentication";

	String SERVICES_LDAP_AUTHORISATION = "services.ldap.authorisation";

	String SERVICES_CC_ENABLED = "services.cc";

	String SERVICES_CC_AMEX_ENABLED = "services.cc.amex";

	String SERVICES_INFO_REPLICATION_VERSION = "data.referencedataversion";

	String DEDUPE_LASTRUN = "services.dedupelastrun";

	String SERVICES_INFO_REPLICATION_LASTRUN = "services.inforeplicationlastrun";

	String SERVICES_ANGEL_REPLICATION_LASTRUN = "services.angeloreplicationlastrun";

	// **************************************
	// Licensing
	// **************************************

	String LICENSE_ACCESS_CONTROL = "license.accesscontrol";

	String LICENSE_SMS = "license.sms";

	String LICENSE_CC_PROCESSING = "license.ccprocessing";

	String LICENSE_PAYROLL = "license.payroll";

	String LICENSE_VOUCHER = "license.voucher";

	String LICENSE_MEMBERSHIP = "license.membership";

	String LICENSE_ATTENDANCE = "license.attendance";

	String LICENSE_SCRIPTING = "license.scripting";

	String LICENSE_FEE_HELP_EXPORT = "license.feeHelpExport";

	String LICENSE_FUNDING_CONTRACT = "license.fundingContract";

	String LICENSE_AUTO_PAY = "license.autopay";


	// **************************************
	// Messaging
	// **************************************
	/**
	 * System administrator email address
	 *
	 * @param prefKey email.admin
	 */
	@API
	String EMAIL_ADMIN_ADDRESS = "email.admin";

	/**
	 * Email from address. Outbound email will have this from address by default.
	 *
	 * @param prefKey email.from
	 */
	@API
	String EMAIL_FROM_ADDRESS = "email.from";

	/**
	 * Email from name
	 *
	 * @param prefKey email.from.name
	 */
	@API
	String EMAIL_FROM_NAME = "email.from.name";

	String EMAIL_BOUNCE_ENABLED = "email.bounce";

	/**
	 * The POP server from which bounced email will be retrieved. Use this if you have VERP enabled and want onCourse
	 * to match bounces against your contact list.
	 *
	 * @param prefKey email.pop3host
	 */
	@API
	String EMAIL_POP3HOST = "email.pop3host";

	/**
	 * Email address to which bounces are sent
	 *
	 * @param prefKey email.bounce.address
	 */
	@API
	String EMAIL_BOUNCEADDRESS = "email.bounce.address";

	String EMAIL_POP3ACCOUNT = "email.pop3.account";

	String EMAIL_POP3PASSWORD = "email.pop3.password";

	/**
	 * SMS from. This can either be a valid phone number (in which case recipients will be able to reply directly) or
	 * a word which identifies your college.
	 *
	 * @param prefKey sms.from
	 */
	@API
	String SMS_FROM_ADDRESS = "sms.from";



	// **************************************
	// LDAP
	// **************************************

	// server settings
	/**
	 * LDAP host
	 *
	 * @param prefKey ldap.host
	 */
	@API
	String LDAP_HOST = "ldap.host";

	String LDAP_SERVERPORT = "ldap.serverport";
	@Deprecated
	String LDAP_SECURITY = "ldap.security";
	String LDAP_SSL = "ldap.ssl";
	String LDAP_BIND_USER_DN = "ldap.bind.user.dn";
	String LDAP_BIND_USER_PASS = "ldap.bind.user.pass";
	String LDAP_BASE_DN = "ldap.base.dn";

	// user settings
	String LDAP_USERNAME_ATTRIBUTE = "ldap.username.attibute";
	String LDAP_USER_SEARCH_FILTER = "ldap.user.search.filter";

	// role settigs
	String LDAP_GROUP_ATTRIBUTE = "ldap.group.attibute";
	String LDAP_GROUP_MEMBER_ATTRIBUTE = "ldap.group.member.attibute";
	String LDAP_GROUP_POSIX_STYLE = "ldap.group.posixstyle";
	String LDAP_GROUP_SEARCH_FILTER = "ldap.group.search.filter";

	// below are values, not keys
	String LDAP_SIMPLE_AUTHENTICATION = "simple";
	String LDAP_SASL_AUTHENTICATION = "sasl";

	@Deprecated
	String LDAP_DOMAIN = "ldap.domain";



	// **************************************
	// Maintenance and backup
	// **************************************
	String LOGOUT_ENABLED = "logout.enabled";
	String LOGOUT_TIMEOUT = "logout.timeout";

	// **************************************
	// Account defaults
	// **************************************
	// For account defaults see AccountDefaults class.

	/**
	 * Default currency
	 *
	 * @param prefKey default.currency
	 */
	@API
	String ACCOUNT_CURRENCY = "default.currency";
	String ACCOUNT_TAXPK = "tax.default.pk";

	String PAY_PERIOD_DAYS = "pay_period_type";
	String ACCOUNT_PREPAID_FEES_POST_AT = "account.prepaidFeesPostAt";
	String ACCOUNT_PREPAID_FEES_POST_AT_EVERY_SESSION = "everySession";
	String ACCOUNT_PREPAID_FEES_POST_AT_FIRST_SESSION = "firstSession";

	/**
	 * Default invoice terms (days)
	 *
	 * @param prefKey account.invoice.terms
	 */
	@API
	String ACCOUNT_INVOICE_TERMS = "account.invoice.terms";



	// **************************************
	// AVETMISS
	// **************************************
	String AVETMISS_SHOW_GUI = "enableRTOGUI";

	/**
	 * AVETMISS RTO Identifier. Must be a valid choice from training.gov.au
	 *
	 * @param prefKey avetmiss.identifier
	 */
	@API
	String AVETMISS_ID = "avetmiss.identifier";

	/**
	 * Defaut AVETMISS jurisdiction used when exporting.
	 *
	 * @param prefKey avetmiss.jurisdiction
	 */
	@API
	String AVETMISS_JURISDICTION = "avetmiss.jurisdiction";

	/**
	 * Training organisation name for the AVETMISS export file.
	 *
	 * @param prefKey avetmiss.collegename
	 */
	@API
	String AVETMISS_COLLEGENAME = "avetmiss.collegename";

	String AVETMISS_COLLEGESHORTNAME = "avetmiss.collegeshortname";

	String AVETMISS_TYPE = "avetmiss.type";

	/**
	 * Address 1 line for the AVETMISS export file.
	 *
	 * @param prefKey avetmiss.address.line1
	 */
	@API
	String AVETMISS_ADDRESS1 = "avetmiss.address.line1";

	/**
	 * Address 2 line for the AVETMISS export file.
	 *
	 * @param prefKey avetmiss.address.line2
	 */
	@API
	String AVETMISS_ADDRESS2 = "avetmiss.address.line2";

	/**
	 * Suburb for the AVETMISS export file.
	 *
	 * @param prefKey avetmiss.address.suburb
	 */
	@API
	String AVETMISS_SUBURB = "avetmiss.address.suburb";

	/**
	 * Postcode for the AVETMISS export file.
	 *
	 * @param prefKey avetmiss.address.postcode
	 */
	@API
	String AVETMISS_POSTCODE = "avetmiss.address.postcode";

	/**
	 * State for the AVETMISS export file.
	 *
	 * @param prefKey avetmiss.address.state
	 */
	@API
	String AVETMISS_STATE = "avetmiss.address.state";

	String AVETMISS_STATE_NAME_VirtualKey = "avetmiss.address.stateName";

	/**
	 * Contact name for the AVETMISS export file.
	 *
	 * @param prefKey avetmiss.contactname
	 */
	@API
	String AVETMISS_CONTACTNAME = "avetmiss.contactname";

	/**
	 * Telephone for the AVETMISS export file.
	 *
	 * @param prefKey avetmiss.phone
	 */
	@API
	String AVETMISS_PHONE = "avetmiss.phone";

	/**
	 * Fax for the AVETMISS export file.
	 *
	 * @param prefKey avetmiss.fax
	 */
	@API
	String AVETMISS_FAX = "avetmiss.fax";

	/**
	 * College email for the AVETMISS export file.
	 *
	 * @param prefKey avetmiss.email
	 */
	@API
	String AVETMISS_EMAIL = "avetmiss.email";

	/**
	 * Certificate signatory name. This name is placed at the bottom of the printed certificate.
	 *
	 * @param prefKey avetmiss.certificate.signatory.name
	 */
	@API
	String AVETMISS_CERT_SIGNATORY_NAME = "avetmiss.certificate.signatory.name";

	/**
	 * Queensland RTO id
	 *
	 * @param prefKey avetmiss.qld.identifier
	 */
	@API
	String AVETMISS_QLD_IDENTIFIER = "avetmiss.qld.identifier";

	/**
	 * FEE HELP Provider Code
	 *
	 * @param prefKey avetmiss.fee.help.provider.code
	 */
	@API
	String AVETMISS_FEE_HELP_PROVIDER_CODE = "avetmiss.fee.help.provider.code";



	// **************************************
	// CourseClass preferences
	// **************************************

	/**
	 * Default minimum places in class. Used when creating a new class.
	 *
	 * @param prefKey courseclass_default_minimumPlaces
	 */
	@API
	String CLASS_DEFAULTS_MINIMUM_PLACES = "courseclass_default_minimumPlaces";
	/**
	 * Default maximum places in class. Used when creating a new class.
	 *
	 * @param prefKey courseclass_default_maximumPlaces
	 */
	@API
	String CLASS_DEFAULTS_MAXIMUM_PLACES = "courseclass_default_maximumPlaces";

	String CLASS_DEFAULTS_DELIVERY_MODE = "courseclass_default_deliveryMode";
	String CLASS_DEFAULTS_FUNDING_SOURCE = "courseclass_default_fundingSource";



	// **************************************
	// Other
	// **************************************
	String GRAVATAR = "gravatar.enabled";

	// **************************************
	// Frame preferences
	// **************************************
	String TOOLBAR_COLLAPSE_STATE = "toolbar.collapse";
	String EULA_AGREEMENT = "eula.agreement.";
	String TOOLBAR_ACTIVE_TAB = "toolbar.tab.active";


	// **************************************
	// Login preferences
	// **************************************
	String LASTLOGIN_USERNAME = "oncourse.login.last.username";
	String LASTLOGIN_SERVER_HOST = "oncourse.login.last.host";
	String LASTLOGIN_SERVER_PORT = "oncourse.login.last.port";
	String LASTLOGIN_SERVER_ISSSL = "oncourse.login.last.isssl";

	String USI_SOFTWARE_ID = "usi.softwareid";



	// **************************************
	// Other preferences
	// **************************************
	String USE_ONLY_OFFERED_MODULES_AND_QUALIFICATIONS = "use.offered.qualifications.only";
	String MYOB_LAST_EXPORT_DATE = "myob.last.export.date";



	String FEATURE_CONCESSIONS_IN_ENROLMENT = "feature.concessionsInEnrolment";
	String FEATURE_CONCESSION_USERS_CREATE = "feature.concession.existing.users.create";
	String FEATURE_ENROLMENT_DISCLOSURE = "feature.enrolmentDisclosure";



	// **************************************
	// portal
	// **************************************
	String PORTAL_HIDE_CLASS_ROLL_CONTACT_PHONE = "portal.hideClassRollContactPhone";
	String PORTAL_HIDE_CLASS_ROLL_CONTACT_EMAIL = "portal.hideClassRollContactEmail";



	// **************************************
	// finance
	// **************************************
	String FINANCE_TRANSACTION_LOCKED = "finance.transaction_locked";



	String SERVICES_COMMUNICATION_KEY = "services.soap.communication.key";
	String REPLICATION_ENABLED = "replication.enabled";


	// **************************************
	// File destinations
	// **************************************
	String REPORT_PDF_FOLDER = "report.pdfSaveFolder";
	String REPORT_XLS_FOLDER = "report.xlsSaveFolder";
	String REPORT_IMPORT_FOLDER = "report.jasperImportFolder";
	String EXPORTTEMPLATE_IMPORT_FOLDER = "exporttemplate.xslImportFolder";
	String AVETMISS_EXPORT_PATH = "avetmiss.lastpath";

	String DOCUMENT_IMPORT_PATH = "document.lastpath";


	// **************************************
	// Security
	// **************************************

	/**
	 * Automatically disable inactive accounts. True by default
	 *
	 * @param prefKey security.auto.disable.inactive.account
	 */
	@API
	String AUTO_DISABLE_INACTIVE_ACCOUNT = "security.auto.disable.inactive.account";

	/**
	 * Require better password complexity.
	 *
	 * @param prefKey security.password.complexity
	 */
	@API
	String PASSWORD_COMPLEXITY = "security.password.complexity";

	/**
	 * Require password change after expiry period
	 *
	 * @param prefKey security.password.expiry.period
	 */
	@API
	String PASSWORD_EXPIRY_PERIOD = "security.password.expiry.period";

	/**
	 * Two factor authentication status for users
	 *
	 * @param prefKey security.tfa.status
	 */
	@API
	String TWO_FACTOR_AUTHENTICATION = "security.tfa.status";

	/**
	 * Two factor authentication expiry period
	 *
	 * @param prefKey security.tfa.expiry.period
	 */
	@API
	String TFA_EXPIRY_PERIOD = "security.tfa.expiry.period";

	/**
	 * Automatically disable accounts after <x> incorrect login attempts. 5 by default
	 *
	 * @param prefKey security.number.login.attempts
	 */
	@API
	String NUMBER_OF_LOGIN_ATTEMPTS = "security.number.login.attempts";
}
