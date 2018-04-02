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
public class Preferences {
	
	// **************************************
	// College information
	// **************************************

	/**
	 * College name
	 *
	 * @param prefKey college.name
	 */
	@API
	public static final String COLLEGE_NAME = "college.name";
	
	/**
	 * College ABN
	 *
	 * @param prefKey college.abn
	 */
	@API
	public static final String COLLEGE_ABN = "college.abn";

	/**
	 * College website URL. If you have multiple sites, use the primary one here since this will be used for references
	 * to street directions and other information in an enrolment confirmation.
	 *
	 * @param prefKey web.url
	 */
	@API
	public static final String COLLEGE_URL = "web.url";
	
	/**
	 * Invoice remittance instructions. This additional text will be added to every invoice.
	 *
	 * @param prefKey college.paymentInfo
	 */
	@API
	public static final String COLLEGE_PAYMENT_INFO = "college.paymentInfo";
	
	public static final String COLLEGE_ENROL_SUCCESS_URL = "enrol.postSuccessURL";
	
	

	// **************************************
	// Services
	// **************************************

	/**
	 * Default server time zone. Don't change this without conisidering how the change affects your data.
	 *
	 * @param prefKey oncourse.server.timezone.default
	 */
	@API
	public static final String ONCOURSE_SERVER_DEFAULT_TZ = "oncourse.server.timezone.default";
	
	public static final String SERVICES_LDAP_AUTHENTICATION = "services.ldap.authentication";
	
	public static final String SERVICES_LDAP_AUTHORISATION = "services.ldap.authorisation";
	
	public static final String SERVICES_CC_ENABLED = "services.cc";
	
	public static final String SERVICES_CC_AMEX_ENABLED = "services.cc.amex";
	
	public static final String SERVICES_SECURITYKEY = "services.securitykey";
	
	public static final String DATA_SVNVERSION = "data.svnversion";
	
	public static final String DATA_WED_VERSION = "data.wed.version";
	
	public static final String SERVICES_INFO_REPLICATION_VERSION = "data.referencedataversion";
	
	public static final String DEDUPE_LASTRUN = "services.dedupelastrun";
	
	public static final String SERVICES_INFO_REPLICATION_LASTRUN = "services.inforeplicationlastrun";
	
	public static final String SERVICES_ANGEL_REPLICATION_LASTRUN = "services.angeloreplicationlastrun";
	
	public static final String SERVICES_REPLICATION_REQUEUE_ID = "server.soap.requeueid";
	
	

	// **************************************
	// Licensing
	// **************************************

	public static final String LICENSE_ACCESS_CONTROL = "license.accesscontrol";
	
	public static final String LICENSE_LDAP = "license.ldap";
	
	public static final String LICENSE_BUDGET = "license.budget";
	
	public static final String LICENSE_EXTENRNAL_DB = "license.externaldb";
	
	public static final String LICENSE_SSL = "license.ssl";
	
	public static final String LICENSE_SMS = "license.sms";
	
	public static final String LICENSE_CC_PROCESSING = "license.ccprocessing";
	
	public static final String LICENSE_PAYROLL = "license.payroll";
	
	public static final String LICENSE_VOUCHER = "license.voucher";
	
	public static final String LICENSE_MEMBERSHIP = "license.membership";
	
	public static final String LICENSE_ATTENDANCE = "license.attendance";
	
	public static final String LICENSE_SCRIPTING = "license.scripting";
	
	public static final String LICENSE_FEE_HELP_EXPORT = "license.feeHelpExport";

	public static final String LICENSE_FUNDING_CONTRACT = "license.fundingContract";
	

	// **************************************
	// Messaging
	// **************************************

	/**
	 * Outgoing mail server address (SMTP)
	 *
	 * @param prefKey email.smtphost
	 */
	@API
	public static final String EMAIL_SMTPHOST = "email.smtphost";

	
	/**
	 * System administrator email address
	 *
	 * @param prefKey email.admin
	 */
	@API
	public static final String EMAIL_ADMIN_ADDRESS = "email.admin";

	/**
	 * Email from address. Outbound email will have this from address by default.
	 *
	 * @param prefKey email.from
	 */
	@API
	public static final String EMAIL_FROM_ADDRESS = "email.from";

	/**
	 * Email from name
	 *
	 * @param prefKey email.from.name
	 */
	@API
	public static final String EMAIL_FROM_NAME = "email.from.name";
	
	public static final String EMAIL_BOUNCE_ENABLED = "email.bounce";

	/**
	 * The POP server from which bounced email will be retrieved. Use this if you have VERP enabled and want onCourse
	 * to match bounces against your contact list.
	 *
	 * @param prefKey email.pop3host
	 */
	@API
	public static final String EMAIL_POP3HOST = "email.pop3host";

	/**
	 * Email address to which bounces are sent
	 *
	 * @param prefKey email.bounce.address
	 */
	@API
	public static final String EMAIL_BOUNCEADDRESS = "email.bounce.address";
	
	public static final String EMAIL_POP3ACCOUNT = "email.pop3.account";
	
	public static final String EMAIL_POP3PASSWORD = "email.pop3.password";

	/**
	 * SMS from. This can either be a valid phone number (in which case recipients will be able to reply directly) or
	 * a word which identifies your college.
	 *
	 * @param prefKey sms.from
	 */
	@API
	public static final String SMS_FROM_ADDRESS = "sms.from";

	/**
	 * SMTP username
	 *
	 * @param prefKey smtp.username
	 */
	@API
	public static final String SMTP_USERNAME = "smtp.username";
	
	public static final String SMTP_PASSWORD = "smtp.password";
	
	public static final String SMTP_START_TLS = "mail.smtp.starttls.enable";
	
	public static final String SMTP_PORT = "mail.smtp.port";



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
	public static final String LDAP_HOST = "ldap.host";

	public static final String LDAP_SERVERPORT = "ldap.serverport";
	@Deprecated
	public static final String LDAP_SECURITY = "ldap.security";
	public static final String LDAP_SSL = "ldap.ssl";
	public static final String LDAP_BIND_USER_DN = "ldap.bind.user.dn";
	public static final String LDAP_BIND_USER_PASS = "ldap.bind.user.pass";
	public static final String LDAP_BASE_DN = "ldap.base.dn";

	// user settings
	public static final String LDAP_USERNAME_ATTRIBUTE = "ldap.username.attibute";
	public static final String LDAP_USER_SEARCH_FILTER = "ldap.user.search.filter";

	// role settigs
	public static final String LDAP_GROUP_ATTRIBUTE = "ldap.group.attibute";
	public static final String LDAP_GROUP_MEMBER_ATTRIBUTE = "ldap.group.member.attibute";
	public static final String LDAP_GROUP_POSIX_STYLE = "ldap.group.posixstyle";
	public static final String LDAP_GROUP_SEARCH_FILTER = "ldap.group.search.filter";

	// below are values, not keys
	public static final String LDAP_SIMPLE_AUTHENTICATION = "simple";
	public static final String LDAP_SASL_AUTHENTICATION = "sasl";

	@Deprecated
	public static final String LDAP_DOMAIN = "ldap.domain";

	
	
	// **************************************
	// Maintenance and backup
	// **************************************
	public static final String LOGOUT_ENABLED = "logout.enabled";
	public static final String LOGOUT_TIMEOUT = "logout.timeout";

	public static final String DATABASE_USED = "database.used";
	public static final String DATABASE_USED_DERBY = "database.derby";
	public static final String DATABASE_USED_MYSQL = "database.mysql";
	public static final String DATABASE_USED_MSSQL = "database.mssql";
	public static final String DATABASE_USED_POSTGRE = "database.postgre";

	public static final String BACKUP_ENABLED = "backup.enabled";
	public static final String BACKUP_DIR = "backup.destination";
	public static final String BACKUP_DIR_WARNING = "backup.destination.warning";

	public static final String BACKUP_MAX_HISTORY = "backup.maxhistory";
	public static final String BACKUP_NEXT_NUMBER = "backup.nextnumber";
	public static final String BACKUP_TIMEOFDAY = "backup.minuteofday";



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
	public static final String ACCOUNT_CURRENCY = "default.currency";
	public static final String ACCOUNT_TAXPK = "tax.default.pk";

	public static final String PAY_PERIOD_DAYS = "pay_period_type";
	public static final String ACCOUNT_PREPAID_FEES_POST_AT = "account.prepaidFeesPostAt";
	public static final String ACCOUNT_PREPAID_FEES_POST_AT_EVERY_SESSION = "everySession";
	public static final String ACCOUNT_PREPAID_FEES_POST_AT_FIRST_SESSION = "firstSession";

	/**
	 * Default invoice terms (days)
	 *
	 * @param prefKey account.invoice.terms
	 */
	@API
	public static final String ACCOUNT_INVOICE_TERMS = "account.invoice.terms";



	// **************************************
	// AVETMISS
	// **************************************
	public static final String AVETMISS_SHOW_GUI = "enableRTOGUI";

	/**
	 * AVETMISS RTO Identifier. Must be a valid choice from training.gov.au
	 *
	 * @param prefKey avetmiss.identifier
	 */
	@API
	public static final String AVETMISS_ID = "avetmiss.identifier";

	/**
	 * Defaut AVETMISS jurisdiction used when exporting.
	 *
	 * @param prefKey avetmiss.jurisdiction
	 */
	@API
	public static final String AVETMISS_JURISDICTION = "avetmiss.jurisdiction";

	/**
	 * Training organisation name for the AVETMISS export file.
	 *
	 * @param prefKey avetmiss.collegename
	 */
	@API
	public static final String AVETMISS_COLLEGENAME = "avetmiss.collegename";
	
	public static final String AVETMISS_COLLEGESHORTNAME = "avetmiss.collegeshortname";
	
	public static final String AVETMISS_TYPE = "avetmiss.type";

	/**
	 * Address 1 line for the AVETMISS export file.
	 *
	 * @param prefKey avetmiss.address.line1
	 */
	@API
	public static final String AVETMISS_ADDRESS1 = "avetmiss.address.line1";

	/**
	 * Address 2 line for the AVETMISS export file.
	 *
	 * @param prefKey avetmiss.address.line2
	 */
	@API
	public static final String AVETMISS_ADDRESS2 = "avetmiss.address.line2";

	/**
	 * Suburb for the AVETMISS export file.
	 *
	 * @param prefKey avetmiss.address.suburb
	 */
	@API
	public static final String AVETMISS_SUBURB = "avetmiss.address.suburb";

	/**
	 * Postcode for the AVETMISS export file.
	 *
	 * @param prefKey avetmiss.address.postcode
	 */
	@API
	public static final String AVETMISS_POSTCODE = "avetmiss.address.postcode";

	/**
	 * State for the AVETMISS export file.
	 *
	 * @param prefKey avetmiss.address.state
	 */
	@API
	public static final String AVETMISS_STATE = "avetmiss.address.state";
	
	public static final String AVETMISS_STATE_NAME_VirtualKey = "avetmiss.address.stateName";

	/**
	 * Contact name for the AVETMISS export file.
	 *
	 * @param prefKey avetmiss.contactname
	 */
	@API
	public static final String AVETMISS_CONTACTNAME = "avetmiss.contactname";

	/**
	 * Telephone for the AVETMISS export file.
	 *
	 * @param prefKey avetmiss.phone
	 */
	@API
	public static final String AVETMISS_PHONE = "avetmiss.phone";

	/**
	 * Fax for the AVETMISS export file.
	 *
	 * @param prefKey avetmiss.fax
	 */
	@API
	public static final String AVETMISS_FAX = "avetmiss.fax";

	/**
	 * College email for the AVETMISS export file.
	 *
	 * @param prefKey avetmiss.email
	 */
	@API
	public static final String AVETMISS_EMAIL = "avetmiss.email";

	/**
	 * Certificate signatory name. This name is placed at the bottom of the printed certificate.
	 *
	 * @param prefKey avetmiss.certificate.signatory.name
	 */
	@API
	public static final String AVETMISS_CERT_SIGNATORY_NAME = "avetmiss.certificate.signatory.name";

	/**
	 * Queensland RTO id
	 *
	 * @param prefKey avetmiss.qld.identifier
	 */
	@API
	public static final String AVETMISS_QLD_IDENTIFIER = "avetmiss.qld.identifier";

	/**
	 * FEE HELP Provider Code
	 *
	 * @param prefKey avetmiss.fee.help.provider.code
	 */
	@API
	public static final String AVETMISS_FEE_HELP_PROVIDER_CODE = "avetmiss.fee.help.provider.code";



	// **************************************
	// CourseClass preferences
	// **************************************
	
	/**
	 * Default minimum places in class. Used when creating a new class.
	 *
	 * @param prefKey courseclass_default_minimumPlaces
	 */
	@API
	public static final String CLASS_DEFAULTS_MINIMUM_PLACES = "courseclass_default_minimumPlaces";
	/**
	 * Default maximum places in class. Used when creating a new class.
	 *
	 * @param prefKey courseclass_default_maximumPlaces
	 */
	@API
	public static final String CLASS_DEFAULTS_MAXIMUM_PLACES = "courseclass_default_maximumPlaces";
	
	public static final String CLASS_DEFAULTS_DELIVERY_MODE = "courseclass_default_deliveryMode";
	public static final String CLASS_DEFAULTS_FUNDING_SOURCE = "courseclass_default_fundingSource";



	// **************************************
	// Other
	// **************************************
	public static final String QE_DEFAULTS_TO_ZERO = "qe.payment.default.zero";
	public static final String QE_DEFAULT_REPORT_ENROLMENT_KEYCODE = "qe.default.report.enrolment.keycode";
	public static final String QE_DEFAULT_REPORT_INVOICE_KEYCODE = "qe.default.report.invoice.keycode";

	public static final String GRAVATAR = "gravatar.enabled";



	// **************************************
	// Frame preferences
	// **************************************
	public static final String TOOLBAR_COLLAPSE_STATE = "toolbar.collapse";
	public static final String FRAME_BOUNDS = "frame.bounds.";
	public static final String LISTVIEW_COLUMNS = "listview.columns.";
	public static final String LISTVIEW_DIVIDER = "listview.divider.";
	public static final String QEVIEW_DIVIDER = "qeview.divider.";
	public static final String EULA_AGREEMENT = "eula.agreement.";
	public static final String TOOLBAR_ACTIVE_TAB = "toolbar.tab.active";
	public static final String LISTVIEW_FILTERS_COLLAPSING = "listview.filters.collapsing.";
	public static final String LISTVIEW_COLUMN_SORTED = "listview.column.sorted.";



	// **************************************
	// Login preferences
	// **************************************
	public static final String LASTLOGIN_USERNAME = "oncourse.login.last.username";
	public static final String LASTLOGIN_SERVER_HOST = "oncourse.login.last.host";
	public static final String LASTLOGIN_SERVER_PORT = "oncourse.login.last.port";
	public static final String LASTLOGIN_SERVER_ISSSL = "oncourse.login.last.isssl";

	
	
	// **************************************
	// External storage preferences
	// **************************************
	public static final String STORAGE_BUCKET_NAME = "storage.bucket";
	public static final String STORAGE_ACCESS_ID = "storage.access.id";
	public static final String STORAGE_ACCESS_KEY = "storage.access.key";




	public static final String AUSKEY_PASSWORD = "auskey.password";
	public static final String AUSKEY_CERTIFICATE = "auskey.certificate";
	public static final String AUSKEY_PRIVATE_KEY = "auskey.privatekey";
	public static final String AUSKEY_SALT = "auskey.salt";



	// **************************************
	// Other preferences
	// **************************************
	public static final String USE_ONLY_OFFERED_MODULES_AND_QUALIFICATIONS = "use.offered.qualifications.only";
	public static final String MYOB_LAST_EXPORT_DATE = "myob.last.export.date";



	public static final String FEATURE_CONCESSIONS_IN_ENROLMENT = "feature.concessionsInEnrolment";
	public static final String FEATURE_CONCESSION_USERS_CREATE = "feature.concession.existing.users.create";
	public static final String FEATURE_ENROLMENT_DISCLOSURE = "feature.enrolmentDisclosure";
	
	
	
	// **************************************
	// portal
	// **************************************
	public static final String PORTAL_HIDE_CLASS_ROLL_CONTACT_PHONE = "portal.hideClassRollContactPhone";
	public static final String PORTAL_HIDE_CLASS_ROLL_CONTACT_EMAIL = "portal.hideClassRollContactEmail";

	
	
	// **************************************
	// finance
	// **************************************
	public static final String FINANCE_TRANSACTION_LOCKED = "finance.transaction_locked";

	
	
	public static final String SERVICES_COMMUNICATION_KEY = "services.soap.communication.key";
	public static final String REPLICATION_ENABLED = "replication.enabled";




	public static final String QE_DEFAULT_REPORT_ENROLMENT = "ish.onCourse.enrolmentConfirmation";
	public static final String QE_DEFAULT_REPORT_INVOICE = "ish.onCourse.invoiceReport";



	// **************************************
	// Printing
	// **************************************
	public static final String REPORT_OVERLAY_PREFIX = "report_overlay_";
	public static final String DEFAULT_REPORT_OVERLAY = "report_overlay_default";



	// **************************************
	// File destinations
	// **************************************
	public static final String MAILINGLIST_EXPORT_FOLDER = "report.mailingListExport";
	public static final String REPORT_PDF_FOLDER = "report.pdfSaveFolder";
	public static final String REPORT_XLS_FOLDER = "report.xlsSaveFolder";
	public static final String REPORT_IMPORT_FOLDER = "report.jasperImportFolder";
	public static final String EXPORTTEMPLATE_IMPORT_FOLDER = "exporttemplate.xslImportFolder";
	public static final String AVETMISS_EXPORT_PATH = "avetmiss.lastpath";

	public static final String DOCUMENT_IMPORT_PATH = "document.lastpath";
}
