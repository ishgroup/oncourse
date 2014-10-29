/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.persistence;

import ish.common.types.ClassFundingSource;
import ish.common.types.CreditCardType;
import ish.common.types.DeliveryMode;
import ish.common.types.TypesUtil;
import ish.math.Country;
import ish.oncourse.common.ExportJurisdiction;
import ish.util.Maps;
import ish.util.SecurityUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.awt.*;
import java.io.*;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

/**
 * This abstract class is implemented in each of the client and the server. This is needed because the persistent object classes are different in those two
 * places and access to them needs to be implemented separately.
 */
public abstract class CommonPreferenceController {

	private static final Logger logger = Logger.getLogger(CommonPreferenceController.class);

	private static final Preferences FILE_PREFS = Preferences.userNodeForPackage(CommonPreferenceController.class);
	public static final Pattern NO_WHITESPACE_PATTERN = Pattern.compile("^[^\\s]+$");
	// use US locale so months in English
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.US);
	private static final Pattern commaExplode = Pattern.compile(",");
	private static final Pattern colonExplode = Pattern.compile(":");

	private static final List<String> DEPRECATED_PREFERENCES = new ArrayList<>();

	static {
		DEPRECATED_PREFERENCES.add("print.header");
	}

	public static final String QE_DEFAULT_REPORT_ENROLMENT = "ish.onCourse.enrolmentConfirmation";
	public static final String QE_DEFAULT_REPORT_INVOICE = "ish.onCourse.invoiceReport";

	protected static CommonPreferenceController sharedController;

	// **************************************
	// College information
	// **************************************
	public static final String COLLEGE_NAME = "college.name";
	public static final String COLLEGE_ABN = "college.abn";
	public static final String COLLEGE_URL = "web.url";
	public static final String COLLEGE_PAYMENT_INFO = "college.paymentInfo";
	public static final String COLLEGE_ENROL_SUCCESS_URL = "enrol.postSuccessURL";
	/**
	 * <pre>
	 * state identifier p95
	 * 01 NSW
	 * 02 VIC
	 * 03 QLD
	 * 04 SA
	 * 05 WA
	 * 06 TAS
	 * 07 NT
	 * 08 ACT
	 * 09 other Australian territories
	 * 99 other
	 * </pre>
	 */
	public static final Map<String, String> AddressStates = Maps.asLinkedMap(new String[] {
			"NSW",
			"VIC",
			"QLD",
			"SA",
			"WA",
			"TAS",
			"NT",
			"ACT",
			"Other Australian Territory",
			"Other" }, new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "99" });

	/**
	 * Get the name this college is known as.
	 * 
	 * @return college name
	 */
	public synchronized String getCollegeName() {
		return getValue(COLLEGE_NAME, false);
	}

	/**
	 * Set a name for this college.
	 * 
	 * @param value college name
	 */
	public synchronized void setCollegeName(String value) {
		setValue(COLLEGE_NAME, false, value);
	}

	/**
	 * Get the college ABN (Australian Business Number)
	 * 
	 * @return ABN
	 */
	public synchronized String getCollegeABN() {
		return getValue(COLLEGE_ABN, false);
	}

	/**
	 * Set the college ABN
	 * 
	 * @param value ABN
	 */
	public synchronized void setCollegeABN(String value) {
		setValue(COLLEGE_ABN, false, value);
	}

	/**
	 * Get the web site URL for this college. Does not include http://
	 * 
	 * @return URL string
	 */
	public synchronized String getCollegeURL() {
		return getValue(COLLEGE_URL, false);
	}

	/**
	 * Set the web site URL for this college. Do not include http://
	 * 
	 * @param value URL string
	 */
	public synchronized void setCollegeURL(String value) {
		setValue(COLLEGE_URL, false, value);
	}

	/**
	 * Get the description to put on invoice and help to pay
	 * 
	 * @return URL string
	 */
	public synchronized String getPaymentInfo() {
		return getValue(COLLEGE_PAYMENT_INFO, false);
	}

	/**
	 * Set the description to put on invoice and help to pay
	 * 
	 * @param value URL string
	 */
	public synchronized void setPaymentInfo(String value) {
		setValue(COLLEGE_PAYMENT_INFO, false, value);
	}

	/**
	 * Gets the url to redirect after the successful enrolment.
	 * 
	 * @return URL string
	 */
	public synchronized String getEnrolSuccessUrl() {
		return getValue(COLLEGE_ENROL_SUCCESS_URL, false);
	}

	/**
	 * Sets the url to redirect after the successful enrolment.
	 * 
	 * @param value URL string
	 */
	public synchronized void setEnrolSuccessUrl(String value) {
		setValue(COLLEGE_ENROL_SUCCESS_URL, false, value);
	}

	// **************************************
	// Services
	// **************************************
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

	/**
	 * @return the timezone ID or null
	 */
	public synchronized String getOncourseServerDefaultTimezone() {
		return getValue(ONCOURSE_SERVER_DEFAULT_TZ, false);
	}

	/**
	 * @param timezoneID the timezone ID to set as the default timezone for onCourse
	 */
	public synchronized void setOncourseServerDefaultTimezone(String timezoneID) {
		setValue(ONCOURSE_SERVER_DEFAULT_TZ, false, timezoneID);
	}

	/**
	 * A randomly generated key which is unique for this data file.
	 * 
	 * @return 16 character key
	 */
	public synchronized String getServicesSecurityKey() {
		String value = getValue(SERVICES_SECURITYKEY, false);
		if (value == null) {
			value = SecurityUtil.generateRandomPassword(16);
			setServicesSecurityKey(value);
		}
		return value;
	}

	/**
	 * Set the security key. It should only be set by getServicesSecurityKey()
	 * 
	 * @param value a random key
	 */
	public synchronized void setServicesSecurityKey(String value) {
		setValue(SERVICES_SECURITYKEY, false, value);
	}

	public synchronized boolean getServicesLdapAuthentication() {
		return Boolean.parseBoolean(getValue(SERVICES_LDAP_AUTHENTICATION, false));
	}

	public synchronized void setServicesLdapAuthentication(boolean value) {
		setValue(SERVICES_LDAP_AUTHENTICATION, false, Boolean.toString(value));
	}

	public synchronized boolean getServicesLdapAuthorisation() {
		return Boolean.parseBoolean(getValue(SERVICES_LDAP_AUTHORISATION, false));
	}

	public synchronized void setServicesLdapAuthorisation(boolean value) {
		setValue(SERVICES_LDAP_AUTHORISATION, false, Boolean.toString(value));
	}

	public synchronized boolean getServicesCCEnabled() {
		return Boolean.parseBoolean(getValue(SERVICES_CC_ENABLED, false));
	}

	public synchronized void setServicesCCEnabled(boolean value) {
		setValue(SERVICES_CC_ENABLED, false, Boolean.toString(value));
	}

	public synchronized boolean getServicesAmexEnabled() {
		return Boolean.parseBoolean(getValue(SERVICES_CC_AMEX_ENABLED, false));
	}

	public synchronized BigInteger getReplicationRequeueId() {
		String value = getValue(SERVICES_REPLICATION_REQUEUE_ID, false);
		return value == null ? null : new BigInteger(value);
	}

	public synchronized void setReplicationRequeueId(BigInteger value) {
		setValue(SERVICES_REPLICATION_REQUEUE_ID, false, value.toString());
	}

	public synchronized int getDataSVN() {
		String result = getValue(DATA_SVNVERSION, false);
		if (result == null) {
			return -1;
		}
		return Integer.parseInt(result);
	}

	public synchronized void setDataSVN(int value) {
		setValue(DATA_SVNVERSION, false, Integer.toString(value));
	}

	/** this is not a wed version anymore, but I dont wanna change the preferences. marcin **/
	public synchronized String getDataVersion() {
		return getValue(DATA_WED_VERSION, false);
	}

	public synchronized void setDataVersion(String value) {
		setValue(DATA_WED_VERSION, false, value);
	}

	public synchronized Long getReferenceDataVersion() {
		String result = getValue(SERVICES_INFO_REPLICATION_VERSION, false);
		if (result == null) {
			return -1L;
		}
		return Long.parseLong(result);
	}

	public synchronized void setReferenceDataVersion(long value) {
		setValue(SERVICES_INFO_REPLICATION_VERSION, false, Long.toString(value));
	}

	public synchronized Date getDedupeLastRun() {
		try {
			return dateFormat.parse(getValue(DEDUPE_LASTRUN, false));
		} catch (ParseException e) {
			return null;
		}
	}

	public synchronized void setDedupeLastRun(Date value) {
		setValue(DEDUPE_LASTRUN, false, dateFormat.format(value));
	}

	public synchronized Date getInfoReplicationLastRun() {
		if (getValue(SERVICES_ANGEL_REPLICATION_LASTRUN, false) == null) {
			return null;
		}
		try {
			return dateFormat.parse(getValue(SERVICES_INFO_REPLICATION_LASTRUN, false));
		} catch (ParseException e) {
			return null;
		}
	}

	public synchronized void setInfoReplicationLastRun(Date value) {
		setValue(SERVICES_INFO_REPLICATION_LASTRUN, false, dateFormat.format(value));
	}

	public synchronized Date getAngelReplicationLastRun() {
		if (getValue(SERVICES_ANGEL_REPLICATION_LASTRUN, false) == null) {
			return null;
		}
		try {
			return dateFormat.parse(getValue(SERVICES_ANGEL_REPLICATION_LASTRUN, false));
		} catch (ParseException e) {
			return null;
		}
	}

	public synchronized void setAngelReplicationLastRun(Date value) {
		setValue(SERVICES_ANGEL_REPLICATION_LASTRUN, false, dateFormat.format(value));
	}

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
	public static final String LICENSE_WEBSITE = "license.website";
	public static final String LICENSE_VOUCHER = "license.voucher";
	public static final String LICENSE_MEMBERSHIP = "license.membership";
	public static final String LICENSE_ATTENDANCE = "license.attendance";
	public static final String LICENSE_SCRIPTING = "license.scripting";
	public static final String LICENSE_FEE_HELP_EXPORT = "license.feeHelpExport";
	
	public static boolean LICENSE_BYPASS_MODE = false;

	public synchronized boolean getLicenseAccessControl() {
		return LICENSE_BYPASS_MODE || Boolean.parseBoolean(getValue(LICENSE_ACCESS_CONTROL, false));
	}

	public synchronized void setLicenseAccessControl(boolean value) {
		throw new IllegalStateException("Licences must replicate from ish");
// setValue(LICENSE_ACCESS_CONTROL, false, Boolean.toString(value));
	}

	public synchronized boolean getLicenseLdap() {
		return LICENSE_BYPASS_MODE || Boolean.parseBoolean(getValue(LICENSE_LDAP, false));
	}

	public synchronized void setLicenseLdap(boolean value) {
		throw new IllegalStateException("Licences must replicate from ish");
// setValue(LICENSE_LDAP, false, Boolean.toString(value));
	}

	public synchronized boolean getLicenseBudget() {
		return LICENSE_BYPASS_MODE || Boolean.parseBoolean(getValue(LICENSE_BUDGET, false));
	}

	public synchronized void setLicenseBudget(boolean value) {
		throw new IllegalStateException("Licences must replicate from ish");
// setValue(LICENSE_BUDGET, false, Boolean.toString(value));
	}

	public synchronized boolean getLicenseExternalDB() {
		return LICENSE_BYPASS_MODE || Boolean.parseBoolean(getValue(LICENSE_EXTENRNAL_DB, false));
	}

	public synchronized void setLicenseExternalDB(boolean value) {
		throw new IllegalStateException("Licences must replicate from ish");
// setValue(LICENSE_EXTENRNAL_DB, false, Boolean.toString(value));
	}

	/**
	 * @deprecated - not used since angel 4.1
	 */
	@Deprecated
	public synchronized boolean getLicenseSSL() {
		return LICENSE_BYPASS_MODE || Boolean.parseBoolean(getValue(LICENSE_SSL, false));
	}

	/**
	 * @deprecated - not used since angel 4.1
	 */
	@Deprecated
	public synchronized void setLicenseSSL(boolean value) {
		throw new IllegalStateException("Licences must replicate from ish");
// setValue(LICENSE_SSL, false, Boolean.toString(value));
	}

	public synchronized boolean getLicenseSms() {
		return Boolean.parseBoolean(getValue(LICENSE_SMS, false));
	}

	public synchronized void setLicenseSms(boolean value) {
		throw new IllegalStateException("Licences must replicate from ish");
// setValue(LICENSE_SMS, false, Boolean.toString(value));
	}

	public synchronized boolean getLicenseCCProcessing() {
		return Boolean.parseBoolean(getValue(LICENSE_CC_PROCESSING, false));
	}

	public synchronized void setLicenseCCProcessing(boolean value) {
		throw new IllegalStateException("Licences must replicate from ish");
// setValue(LICENSE_CC_PROCESSING, false, Boolean.toString(value));
	}

	public synchronized boolean getLicensePayroll() {
		return LICENSE_BYPASS_MODE || Boolean.parseBoolean(getValue(LICENSE_PAYROLL, false));
	}

	public synchronized void setLicensePayroll(boolean value) {
		throw new IllegalStateException("Licences must replicate from ish");
// setValue(LICENSE_WEBSITE, false, Boolean.toString(value));
	}

	public synchronized boolean getLicenseWebsite() {
		return Boolean.parseBoolean(getValue(LICENSE_WEBSITE, false));
	}

	public synchronized void setLicenseWebsite(boolean value) {
		throw new IllegalStateException("Licences must replicate from ish");
// setValue(LICENSE_WEBSITE, false, Boolean.toString(value));
	}
	
	public synchronized boolean getLicenseVoucher() {
		return LICENSE_BYPASS_MODE || Boolean.parseBoolean(getValue(LICENSE_VOUCHER, false));
	}
	
	public synchronized void setLicenseVoucher(boolean value) {
		throw new IllegalStateException("Licences must replicate from ish");
	}

	public synchronized boolean getLicenseMembership() {
		return LICENSE_BYPASS_MODE || Boolean.parseBoolean(getValue(LICENSE_MEMBERSHIP, false));
	}

	public synchronized void setLicenseMembership(boolean value) {
		throw new IllegalStateException("Licences must replicate from ish");
	}

	public synchronized boolean getLicenseAttendance() {
		return LICENSE_BYPASS_MODE || Boolean.parseBoolean(getValue(LICENSE_ATTENDANCE, false));
	}

	public synchronized void setLicenseAttendance(boolean value) {
		throw new IllegalStateException("Licences must replicate from ish");
	}
	
	public synchronized boolean getLicenseScripting() {
		return LICENSE_BYPASS_MODE || Boolean.parseBoolean(getValue(LICENSE_SCRIPTING, false));
	}
	
	public synchronized boolean getLicenseFeeHelpExport () {
		return LICENSE_BYPASS_MODE || Boolean.parseBoolean(getValue(LICENSE_FEE_HELP_EXPORT, false));
	}
	
	public synchronized void setLicenseScripting(boolean value) {
		throw new IllegalStateException("Licences must replicate from ish");
	}

	// **************************************
	// Messaging
	// **************************************

	public static final String EMAIL_SMTPHOST = "email.smtphost";
	public static final String EMAIL_ADMIN_ADDRESS = "email.admin";
	public static final String EMAIL_FROM_ADDRESS = "email.from";
	public static final String EMAIL_FROM_NAME = "email.from.name";
	public static final String EMAIL_BOUNCE_ENABLED = "email.bounce";
	public static final String EMAIL_POP3HOST = "email.pop3host";
	public static final String EMAIL_BOUNCEADDRESS = "email.bounce.address";
	public static final String EMAIL_POP3ACCOUNT = "email.pop3.account";
	public static final String EMAIL_POP3PASSWORD = "email.pop3.password";
	public static final String SMS_FROM_ADDRESS = "sms.from";

	public synchronized String getEmailSMTPHost() {
		return getValue(EMAIL_SMTPHOST, false);
	}

	public synchronized void setEmailSMTPHost(String value) {
		setValue(EMAIL_SMTPHOST, false, value);
	}

	public synchronized String getEmailAdminAddress() {
		return getValue(EMAIL_ADMIN_ADDRESS, false);
	}

	public synchronized void setEmailAdminAddress(String value) {
		setValue(EMAIL_ADMIN_ADDRESS, false, value);
	}

	public synchronized String getEmailFromAddress() {
		return getValue(EMAIL_FROM_ADDRESS, false);
	}

	public synchronized void setEmailFromAddress(String value) {
		setValue(EMAIL_FROM_ADDRESS, false, value);
	}

	public synchronized String getEmailFromName() {
		return getValue(EMAIL_FROM_NAME, false);
	}

	public synchronized void setEmailFromName(String value) {
		setValue(EMAIL_FROM_NAME, false, value);
	}

	public synchronized boolean getEmailBounceEnabled() {
		String aPref = getValue(EMAIL_BOUNCE_ENABLED, false);
		if (aPref == null) {
			setEmailBounceEnabled(false);
			return getEmailBounceEnabled();
		}
		return Boolean.parseBoolean(aPref);
	}

	public synchronized void setEmailBounceEnabled(boolean value) {
		setValue(EMAIL_BOUNCE_ENABLED, false, Boolean.toString(value));
	}

	public synchronized String getEmailPOP3Host() {
		return getValue(EMAIL_POP3HOST, false);
	}

	public synchronized void setEmailPOP3Host(String value) {
		setValue(EMAIL_POP3HOST, false, value);
	}

	public synchronized String getEmailBounceAddress() {
		return getValue(EMAIL_BOUNCEADDRESS, false);
	}

	public synchronized void setEmailBounceAddress(String value) {
		setValue(EMAIL_BOUNCEADDRESS, false, value);
	}

	public synchronized String getEmailPOP3Account() {
		return getValue(EMAIL_POP3ACCOUNT, false);
	}

	public synchronized void setEmailPOP3Account(String value) {
		setValue(EMAIL_POP3ACCOUNT, false, value);
	}

	public synchronized String getEmailPOP3Password() {
		return getValue(EMAIL_POP3PASSWORD, false);
	}

	public synchronized void setEmailPOP3Password(String value) {
		setValue(EMAIL_POP3PASSWORD, false, value);
	}

	public synchronized String getSMSFromAddress() {
		return getValue(SMS_FROM_ADDRESS, false);
	}

	public synchronized void setSMSFromAddress(String value) {
		setValue(SMS_FROM_ADDRESS, false, value);
	}

	// **************************************
	// LDAP
	// **************************************
	// server settings
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

	public static final Map<String, String> Ldap_SecurityTypes = Maps.asLinkedMap(new String[] {
			"Simple Authentication (unencrypted connection)",
			"SASL Authentication (secure connection)" }, new String[] { LDAP_SIMPLE_AUTHENTICATION, LDAP_SASL_AUTHENTICATION });

	public synchronized String getLdapHost() {
		return getValue(LDAP_HOST, false);
	}

	public synchronized void setLdapHost(String value) {
		setValue(LDAP_HOST, false, value);
	}

	@Deprecated
	public synchronized String getLdapDomain() {
		return getValue(LDAP_DOMAIN, false);
	}

	@Deprecated
	public synchronized void setLdapDomain(String value) {
		setValue(LDAP_DOMAIN, false, value);
	}

	public synchronized int getLdapServerport() {
		String port = getValue(LDAP_SERVERPORT, false);
		if (port == null) {
			// set default value
			setLdapServerport(389);
			return getLdapServerport();
		}
		return Integer.parseInt(port);
	}

	public synchronized void setLdapServerport(int value) {
		setValue(LDAP_SERVERPORT, false, Integer.valueOf(value).toString());
	}

	@Deprecated
	public synchronized String getLdapSecurity() {
		String security = getValue(LDAP_SECURITY, false);
		if (security == null) {
			// set default value
			setLdapSecurity(LDAP_SIMPLE_AUTHENTICATION);
			return getLdapSecurity();
		}
		return security;
	}

	@Deprecated
	public synchronized void setLdapSecurity(String value) {
		setValue(LDAP_SECURITY, false, value);
	}

	public synchronized String getLdapBindUserDn() {
		return getValue(LDAP_BIND_USER_DN, false);
	}

	public synchronized void setLdapBindUserDn(String value) {
		setValue(LDAP_BIND_USER_DN, false, value);
	}

	public synchronized String getLdapBindUserPass() {
		return getValue(LDAP_BIND_USER_PASS, false);
	}

	public synchronized void setLdapBindUserPass(String value) {
		setValue(LDAP_BIND_USER_PASS, false, value);
	}

	public synchronized Boolean getLdapSSL() {
		try {
			return Boolean.parseBoolean(getValue(LDAP_SSL, false));
		} catch (Exception e) {
			return false;
		}
	}

	public synchronized void setLdapSSL(Boolean value) {
		setValue(LDAP_BIND_USER_DN, false, String.valueOf(value));
	}

	public synchronized String getLdapBaseDN() {
		return getValue(LDAP_BASE_DN, false);
	}

	public synchronized void setLdapBaseDN(String value) {
		setValue(LDAP_BASE_DN, false, value);
	}

	public synchronized String getLdapGroupSearchFilter() {
		return getValue(LDAP_GROUP_SEARCH_FILTER, false);
	}

	public synchronized void setLdapGroupSearchFilter(String value) {
		setValue(LDAP_GROUP_SEARCH_FILTER, false, value);
	}

	public synchronized String getLdapGroupAttribute() {
		return getValue(LDAP_GROUP_ATTRIBUTE, false);
	}

	public synchronized void setLdapGroupAttribute(String value) {
		setValue(LDAP_GROUP_ATTRIBUTE, false, value);
	}

	public synchronized String getLdapGroupMemberAttribute() {
		return getValue(LDAP_GROUP_MEMBER_ATTRIBUTE, false);
	}

	public synchronized void setLdapGroupMemberAttribute(String value) {
		setValue(LDAP_GROUP_MEMBER_ATTRIBUTE, false, value);
	}

	public synchronized Boolean getLdapGroupPosixStyle() {
		try {
			return Boolean.parseBoolean(getValue(LDAP_GROUP_POSIX_STYLE, false));
		} catch (Exception e) {
			return false;
		}
	}

	public synchronized void setLdapGroupPosixStyle(Boolean value) {
		setValue(LDAP_GROUP_POSIX_STYLE, false, String.valueOf(value));
	}

	public synchronized String getLdapUserSearchFilter() {
		return getValue(LDAP_USER_SEARCH_FILTER, false);
	}

	public synchronized void setLdapUserSearchFilter(String value) {
		setValue(LDAP_USER_SEARCH_FILTER, false, value);
	}

	public synchronized String getLdapUsernameAttribute() {
		return getValue(LDAP_USERNAME_ATTRIBUTE, false);
	}

	public synchronized void setLdapUsernameAttribute(String value) {
		setValue(LDAP_USERNAME_ATTRIBUTE, false, value);
	}

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

	public synchronized boolean getBackupEnabled() {
		String aPref = getValue(BACKUP_ENABLED, false);
		if (aPref == null) {
			setBackupEnabled(true);
			setBackupOnMinuteOfDay(60);
			setBackupNextNumber(1);
			setBackupMaxNumber(7);
			setBackupDir(""); // sets backup warning to empty string also.
		}

		return Boolean.parseBoolean(aPref);
	}

	public synchronized void setBackupEnabled(boolean value) {
		setValue(BACKUP_ENABLED, false, Boolean.toString(value));
	}

	public synchronized Integer getBackupOnMinuteOfDay() {
		try {
			return Integer.parseInt(getValue(BACKUP_TIMEOFDAY, false));
		} catch (Exception e) {
			return null;
		}
	}

	public synchronized void setBackupOnMinuteOfDay(int value) {
		setValue(BACKUP_TIMEOFDAY, false, String.valueOf(value));
	}

	public synchronized boolean getLogoutEnabled() {
		String aPref = getValue(LOGOUT_ENABLED, false);
		if (aPref == null) {
			return false;
		}
		return Boolean.parseBoolean(aPref);

	}

	public synchronized void setLogoutEnabled(boolean value) {
		setValue(LOGOUT_ENABLED, false, Boolean.toString(value));
	}

	/**
	 * How long before the client logs out in seconds.
	 * 
	 * @return seconds before logout due to inactivity
	 */
	public synchronized long getLogoutTimeout() {
		String timeout = getValue(LOGOUT_TIMEOUT, false);
		if (timeout == null || timeout.length() == 0) {
			return 0l;
		}

		long time = 0;
		try {
			time = Long.parseLong(timeout);
		} catch (NumberFormatException e) {}
		if (time < 0) {
			return 0;

		}
		return time;
	}

	public synchronized void setLogoutTimeout(String value) {
		setValue(LOGOUT_TIMEOUT, false, value);
	}

	public synchronized String getBackupDir() {
		return getValue(BACKUP_DIR, false);
	}

	public synchronized void setBackupDir(String value) {
		setValue(BACKUP_DIR, false, value);
		setBackupDirWarning(""); // clear warning message
	}

	public synchronized String getBackupDirWarning() {
		return getValue(BACKUP_DIR_WARNING, false);
	}

	public synchronized void setBackupDirWarning(String message) {
		setValue(BACKUP_DIR_WARNING, false, message);
	}

	public synchronized Integer getBackupNextNumber() {
		try {
			return Integer.parseInt(getValue(BACKUP_NEXT_NUMBER, false));
		} catch (Exception e) {
			return null;
		}
	}

	public synchronized void setBackupNextNumber(int value) {
		setValue(BACKUP_NEXT_NUMBER, false, String.valueOf(value));
	}

	public synchronized Integer getBackupMaxNumber() {
		try {
			return Integer.parseInt(getValue(BACKUP_MAX_HISTORY, false));
		} catch (Exception e) {
			return null;
		}
	}

	public synchronized void setBackupMaxNumber(int value) {
		setValue(BACKUP_MAX_HISTORY, false, String.valueOf(value));
	}

	public synchronized String getDatabaseUsed() {
		return getValue(DATABASE_USED, false);
	}

	public synchronized void setDatabaseUsed(String value) {
		setValue(DATABASE_USED, false, value);
	}

	// **************************************
	// Account defaults
	// **************************************
	// For account defaults see AccountDefaults class.

	public static final String ACCOUNT_CURRENCY = "default.currency";
	public static final String ACCOUNT_TAXPK = "tax.default.pk";

	public static final String PAY_PERIOD_DAYS = "pay_period_type";
	public static final String ACCOUNT_PREPAID_FEES_POST_AT = "account.prepaidFeesPostAt";
	public static final String ACCOUNT_PREPAID_FEES_POST_AT_EVERY_SESSION = "everySession";
	public static final String ACCOUNT_PREPAID_FEES_POST_AT_FIRST_SESSION = "firstSession";

	public synchronized Long getDefaultAccountId(String preferenceName) {
		String value = getValue(preferenceName, false);
		if (value == null) {
			return null;
		}
		return Long.valueOf(value);
	}

	public synchronized void setDefaultAccountId(String preferenceName, Long value) {
		setValue(preferenceName, false, String.valueOf(value));
	}


	public synchronized Country getCountry() {
		String result = getValue(ACCOUNT_CURRENCY, false);
		if (result == null) {
			return Country.AUSTRALIA;
		}
		return Country.forCurrencySymbol(result);
	}

	public synchronized void setCountry(Country value) {
		setValue(ACCOUNT_CURRENCY, false, value.currencySymbol());
	}

	public synchronized Long getTaxPK() {
		String result = getValue(ACCOUNT_TAXPK, false);
		if (result == null) {
			return null;
		}
		return Long.valueOf(result);
	}

	public synchronized void setTaxPK(Long value) {
		setValue(ACCOUNT_TAXPK, false, value.toString());
	}

	public synchronized Integer getPayPeriodDays() {
		try {
			String value = getValue(PAY_PERIOD_DAYS, false);
			if (value == null) {
				setPayPeriodDays(14);
				return 14;
			}
			return Integer.parseInt(value);
		} catch (Exception e) {
			return null;
		}
	}

	public synchronized void setPayPeriodDays(int value) {
		setValue(PAY_PERIOD_DAYS, false, String.valueOf(value));
	}

	public synchronized String getAccountPrepaidFeesPostAt() {
		String value = getValue(ACCOUNT_PREPAID_FEES_POST_AT, false);
		if (value == null) {
			setAccountPrepaidFeesPostAt(ACCOUNT_PREPAID_FEES_POST_AT_EVERY_SESSION);
			return ACCOUNT_PREPAID_FEES_POST_AT_EVERY_SESSION;
		}
		return value;
	}

	public synchronized void setAccountPrepaidFeesPostAt(String value) {
		setValue(ACCOUNT_PREPAID_FEES_POST_AT, false, value);
	}

	// **************************************
	// AVETMISS
	// **************************************
	public static final String AVETMISS_SHOW_GUI = "enableRTOGUI";

	public static final String AVETMISS_ID = "avetmiss.identifier";
	public static final String AVETMISS_JURISDICTION = "avetmiss.jurisdiction";
	public static final String AVETMISS_COLLEGENAME = "avetmiss.collegename";
	public static final String AVETMISS_COLLEGESHORTNAME = "avetmiss.collegeshortname";
	public static final String AVETMISS_TYPE = "avetmiss.type";
	public static final String AVETMISS_ADDRESS1 = "avetmiss.address.line1";
	public static final String AVETMISS_ADDRESS2 = "avetmiss.address.line2";
	public static final String AVETMISS_SUBURB = "avetmiss.address.suburb";
	public static final String AVETMISS_POSTCODE = "avetmiss.address.postcode";
	public static final String AVETMISS_STATE = "avetmiss.address.state";
	public static final String AVETMISS_STATE_NAME_VirtualKey = "avetmiss.address.stateName";
	public static final String AVETMISS_CONTACTNAME = "avetmiss.contactname";
	public static final String AVETMISS_PHONE = "avetmiss.phone";
	public static final String AVETMISS_FAX = "avetmiss.fax";
	public static final String AVETMISS_EMAIL = "avetmiss.email";
	public static final String AVETMISS_CERT_SIGNATORY_NAME = "avetmiss.certificate.signatory.name";

	public synchronized boolean getShowRTOGUI() {
		String aPref = getValue(AVETMISS_SHOW_GUI, false);
		if (aPref == null) {
			return false;
		}
		return Boolean.parseBoolean(aPref);

	}

	public synchronized void setShowRTOGUI(boolean value) {
		setValue(AVETMISS_SHOW_GUI, false, Boolean.toString(value));
	}

	public synchronized String getAvetmissID() {
		return getValue(AVETMISS_ID, false);
	}

	public synchronized void setAvetmissID(String value) {
		setValue(AVETMISS_ID, false, value);
	}

	public synchronized ExportJurisdiction getAvetmissJurisdiction() {
		String s = getValue(AVETMISS_JURISDICTION, false);
		if (s == null) {
			return ExportJurisdiction.PLAIN;
		}
		return TypesUtil.getEnumForDatabaseValue(Integer.parseInt(s), ExportJurisdiction.class);
	}

	public synchronized void setAvetmissJurisdiction(ExportJurisdiction value) {
		if (value == null) {
			setValue(AVETMISS_JURISDICTION, false, ExportJurisdiction.PLAIN.getDatabaseValue().toString());
		} else {
			setValue(AVETMISS_JURISDICTION, false, value.getDatabaseValue().toString());
		}
	}

	public synchronized String getAvetmissCollegeName() {
		return getValue(AVETMISS_COLLEGENAME, false);
	}

	public synchronized void setAvetmissCollegeName(String value) {
		setValue(AVETMISS_COLLEGENAME, false, value);
	}

	public synchronized String getAvetmissCollegeShortName() {
		return getValue(AVETMISS_COLLEGESHORTNAME, false);
	}

	public synchronized void setAvetmissCollegeShortName(String value) {
		setValue(AVETMISS_COLLEGESHORTNAME, false, value);
	}

	public synchronized String getAvetmissType() {
		return getValue(AVETMISS_TYPE, false);
	}

	public synchronized void setAvetmissType(String value) {
		setValue(AVETMISS_TYPE, false, value);
	}

	public synchronized String getAvetmissAddress1() {
		String result = getValue(AVETMISS_ADDRESS1, false);
		if (result == null) {
			setAvetmissAddress1("");
			return getAvetmissAddress1();
		}
		return result;
	}

	public synchronized void setAvetmissAddress1(String value) {
		setValue(AVETMISS_ADDRESS1, false, value);
	}

	public synchronized String getAvetmissAddress2() {
		return getValue(AVETMISS_ADDRESS2, false);
	}

	public synchronized void setAvetmissAddress2(String value) {
		setValue(AVETMISS_ADDRESS2, false, value);
	}

	public synchronized String getAvetmissSuburb() {
		String result = getValue(AVETMISS_SUBURB, false);
		if (result == null) {
			setAvetmissSuburb("");
			return getAvetmissSuburb();
		}
		return result;
	}

	public synchronized void setAvetmissSuburb(String value) {
		setValue(AVETMISS_SUBURB, false, value);
	}

	public synchronized String getAvetmissState() {
		return getValue(AVETMISS_STATE, false);
	}

	public synchronized String getAvetmissStateName() {
		String stateID = getAvetmissState();
		if (stateID != null) {
			for (String name : AddressStates.keySet()) {
				String anID = AddressStates.get(name);
				if (stateID.equals(anID)) {
					return name;
				}
			}
		}
		return "";
	}

	public synchronized void setAvetmissState(String value) {
		setValue(AVETMISS_STATE, false, value);
	}

	public synchronized String getAvetmissPostcode() {
		String result = getValue(AVETMISS_POSTCODE, false);
		if (result == null) {
			setAvetmissPostcode("");
			return getAvetmissPostcode();
		}
		return result;
	}

	public synchronized void setAvetmissPostcode(String value) {
		setValue(AVETMISS_POSTCODE, false, value);
	}

	public synchronized String getAvetmissContactName() {
		return getValue(AVETMISS_CONTACTNAME, false);
	}

	public synchronized void setAvetmissContactName(String value) {
		setValue(AVETMISS_CONTACTNAME, false, value);
	}

	public synchronized String getAvetmissPhone() {
		return getValue(AVETMISS_PHONE, false);
	}

	public synchronized void setAvetmissPhone(String value) {
		setValue(AVETMISS_PHONE, false, value);
	}

	public synchronized String getAvetmissFax() {
		return getValue(AVETMISS_FAX, false);
	}

	public synchronized void setAvetmissFax(String value) {
		setValue(AVETMISS_FAX, false, value);
	}

	public synchronized String getAvetmissEmail() {
		return getValue(AVETMISS_EMAIL, false);
	}

	public synchronized void setAvetmissEmail(String value) {
		setValue(AVETMISS_EMAIL, false, value);
	}

	public synchronized String getAvetmissCertSignatoryName() {
		String result = getValue(AVETMISS_CERT_SIGNATORY_NAME, false);
		if (result == null) {
			setAvetmissCertSignatoryName("");
			return getAvetmissCertSignatoryName();
		}
		return result;
	}

	public synchronized void setAvetmissCertSignatoryName(String value) {
		setValue(AVETMISS_CERT_SIGNATORY_NAME, false, value);
	}

	// **************************************
	// Printing
	// **************************************
	public static final String REPORT_OVERLAY_PREFIX = "report_overlay_";
	public static final String DEFAULT_REPORT_OVERLAY = "report_overlay_default";


	public synchronized void setDefaultReportOverlay(Long overlayId) {
		setValue(DEFAULT_REPORT_OVERLAY, false, overlayId==null? null : overlayId.toString());
	}

	public synchronized Long getDefaultReportOverlay() {
		String value = getValue(DEFAULT_REPORT_OVERLAY, false);
		if (value != null) {
			return Long.valueOf(value);
		}
		return null;
	}

	public synchronized void setReportOverlay(String reportKey, Long overlayId) {
		if (reportKey == null) {
			// defualt value
			setDefaultReportOverlay(overlayId);
		} else {
			setValue(REPORT_OVERLAY_PREFIX + reportKey, false, overlayId==null? null : overlayId.toString());
		}
	}

	public synchronized Long getReportOverlay(String reportKey) {
		if (reportKey == null) {
			// defualt value
			return getDefaultReportOverlay();
		} else {
			String value = getValue(REPORT_OVERLAY_PREFIX + reportKey, false);
			if (value != null) {
				return Long.valueOf(value);
			}
			return null;
		}
	}



	// **************************************
	// CourseClass preferences
	// **************************************
	public static final String CLASS_DEFAULTS_MINIMUM_PLACES = "courseclass_default_minimumPlaces";
	public static final String CLASS_DEFAULTS_MAXIMUM_PLACES = "courseclass_default_maximumPlaces";
	public static final String CLASS_DEFAULTS_DELIVERY_MODE = "courseclass_default_deliveryMode";
	public static final String CLASS_DEFAULTS_FUNDING_SOURCE = "courseclass_default_fundingSource";

	public synchronized Integer getCourseClassDefaultMinimumPlaces() {
		try {
			String value = getValue(CLASS_DEFAULTS_MINIMUM_PLACES, false);
			if (value == null) {
				setCourseClassDefaultMinimumPlaces(1);
				return 1;
			}
			return Integer.parseInt(value);
		} catch (Exception e) {
			return null;
		}
	}

	public synchronized void setCourseClassDefaultMinimumPlaces(Integer value) {
		setValue(CLASS_DEFAULTS_MINIMUM_PLACES, false, String.valueOf(value));
	}

	public synchronized Integer getCourseClassDefaultMaximumPlaces() {
		try {
			String value = getValue(CLASS_DEFAULTS_MAXIMUM_PLACES, false);
			if (value == null) {
				setCourseClassDefaultMaximumPlaces(999);
				return 999;
			}
			return Integer.parseInt(value);
		} catch (Exception e) {
			return null;
		}
	}

	public synchronized void setCourseClassDefaultMaximumPlaces(Integer value) {
		setValue(CLASS_DEFAULTS_MAXIMUM_PLACES, false, String.valueOf(value));
	}

	public synchronized DeliveryMode getCourseClassDefaultDeliveryMode() {
		String s = getValue(CLASS_DEFAULTS_DELIVERY_MODE, false);
		if (s == null) {
			return DeliveryMode.CLASSROOM;
		}
		return TypesUtil.getEnumForDatabaseValue(Integer.parseInt(s), DeliveryMode.class);
	}

	public synchronized void setCourseClassDefaultDeliveryMode(DeliveryMode value) {
		if (value == null) {
			setValue(CLASS_DEFAULTS_DELIVERY_MODE, false, DeliveryMode.NOT_SET.getDatabaseValue().toString());
		} else {
			setValue(CLASS_DEFAULTS_DELIVERY_MODE, false, value.getDatabaseValue().toString());
		}
	}

	public synchronized ClassFundingSource getCourseClassDefaultFundingSource() {
		String s = getValue(CLASS_DEFAULTS_FUNDING_SOURCE, false);
		if (s == null) {
			return ClassFundingSource.DOMESTIC_FULL_FEE;
		}
		return TypesUtil.getEnumForDatabaseValue(Integer.parseInt(s), ClassFundingSource.class);
	}

	public synchronized void setCourseClassDefaultFundingSource(ClassFundingSource value) {
		if (value == null) {
			setValue(CLASS_DEFAULTS_FUNDING_SOURCE, false, ClassFundingSource.DOMESTIC_FULL_FEE.getDatabaseValue().toString());
		} else {
			setValue(CLASS_DEFAULTS_FUNDING_SOURCE, false, value.getDatabaseValue().toString());
		}
	}

	// **************************************
	// Other
	// **************************************
	public static final String QE_DEFAULTS_TO_ZERO = "qe.payment.default.zero";
	public static final String QE_DEFAULT_REPORT_ENROLMENT_KEYCODE = "qe.default.report.enrolment.keycode";
	public static final String QE_DEFAULT_REPORT_INVOICE_KEYCODE = "qe.default.report.invoice.keycode";

	public static final String GRAVATAR = "gravatar.enabled";

	public synchronized boolean getQEDefaultsToZeroPayment() {
		String aPref = getValue(QE_DEFAULTS_TO_ZERO, false);
		return aPref != null && Boolean.parseBoolean(aPref);

	}

	public synchronized void setQEDefaultsToZeroPayment(boolean value) {
		setValue(QE_DEFAULTS_TO_ZERO, false, Boolean.toString(value));
	}

	public synchronized String getDefaultQEEnrolmentReportKeycode() {
		String result = getValue(QE_DEFAULT_REPORT_ENROLMENT_KEYCODE, false);
		if (StringUtils.isEmpty(result)) {
			String defaultValue = QE_DEFAULT_REPORT_ENROLMENT;
			setDefaultQEEnrolmentReportKeycode(defaultValue);
			return defaultValue;
		}
		return result;
	}

	public synchronized void setDefaultQEEnrolmentReportKeycode(String value) {
		setValue(QE_DEFAULT_REPORT_ENROLMENT_KEYCODE, false, value);
	}

	public synchronized String getDefaultQEInvoiceReportKeycode() {
		String result = getValue(QE_DEFAULT_REPORT_INVOICE_KEYCODE, false);
		if (StringUtils.isEmpty(result)) {
			String defaultValue = QE_DEFAULT_REPORT_INVOICE;
			setDefaultQEEnrolmentReportKeycode(defaultValue);
			return defaultValue;
		}
		return result;
	}

	public synchronized void setDefaultQEInvoiceReportKeycode(String value) {
		setValue(QE_DEFAULT_REPORT_INVOICE_KEYCODE, false, value);
	}

	public synchronized boolean getGravatarEnabled() {
		String value = getValue(GRAVATAR, false);
		if (StringUtils.isEmpty(value)) {
			return Boolean.TRUE;
		}
		return Boolean.parseBoolean(value);
	}

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

	public synchronized String getToolbarActiveTab() {
		return getValue(TOOLBAR_ACTIVE_TAB, true);
	}

	public synchronized void setToolbarActiveTab(String toolbarActiveTab) {
		setValue(TOOLBAR_ACTIVE_TAB, true, toolbarActiveTab);
	}

	public synchronized String getEulaAgreement(String userLogin) {
		return getValue(EULA_AGREEMENT + userLogin, true);
	}

	public synchronized void setEulaAgreement(String userLogin, String revision) {
		setValue(EULA_AGREEMENT + userLogin, true, revision);
	}

	public synchronized Rectangle getFramePosition(String frameIdentifier) {
		String pref = getValue(FRAME_BOUNDS + frameIdentifier, true);
		if (pref != null && !pref.equals("")) {
			String[] elements = commaExplode.split(pref);
			if (elements.length == 4) {
				int x = Integer.valueOf(elements[0]);
				int y = Integer.valueOf(elements[1]);
				int w = Integer.valueOf(elements[2]);
				int h = Integer.valueOf(elements[3]);
				return new Rectangle(x, y, w, h);
			}
		}
		return null;
	}

	public synchronized void setFramePosition(String frameIdentifier, Rectangle value) {
		String prefValue = (int) value.getX() + "," + (int) value.getY() + "," + (int) value.getWidth() + "," + (int) value.getHeight();
		setValue(FRAME_BOUNDS + frameIdentifier, true, prefValue);
	}

	public synchronized boolean getToolbarCollapsed() {
		String value = getValue(TOOLBAR_COLLAPSE_STATE, true);
		if (value == null) {
			return false;
		}
		return Boolean.parseBoolean(value);
	}

	public synchronized void setToolbarCollapsed(boolean value) {
		setValue(TOOLBAR_COLLAPSE_STATE, true, Boolean.toString(value));
	}

	/**
	 * Get the list view column order and sizing.
	 * 
	 * @param frameIdentifier a unique string identifier for this view
	 * @return this is a map of column property keys and widths (in pixels)
	 */
	public synchronized Map<String, Integer> getListViewColumns(String frameIdentifier) {
		String pref = getValue(LISTVIEW_COLUMNS + frameIdentifier, true);
		// data is stored like this "COLKEY1:12,COLKEY2:13"

		if (pref != null && !pref.equals("")) {
			String[] elements = commaExplode.split(pref);
			Map<String, Integer> result = new LinkedHashMap<>();
			for (String col : elements) {
				// now split each one around the colon
				String[] colElements = colonExplode.split(col);
				Integer width = colElements.length == 2 ? Integer.valueOf(colElements[1]) : null;
				result.put(colElements[0], width);
			}
			return result;
		}
		return null;
	}

	/**
	 * Set the list view column order and sizing.
	 * 
	 * @param frameIdentifier a unique string identifier for this view
	 * @param value this is a map of column property keys and widths (in pixels)
	 */
	public synchronized void setListViewColumns(String frameIdentifier, Map<String, Integer> value) {
		StringBuilder prefValue = new StringBuilder();
		for (Map.Entry<String, Integer> col : value.entrySet()) {
			prefValue.append(col.getKey()).append(":").append(col.getValue()).append(",");
		}
		prefValue.deleteCharAt(prefValue.length() - 1);
		setValue(LISTVIEW_COLUMNS + frameIdentifier, true, prefValue.toString());
	}

	public synchronized void setListViewDividerPosition(String frameIdentifier, int value) {
		setValue(LISTVIEW_DIVIDER + frameIdentifier, true, String.valueOf(value));
	}

	public synchronized Integer getListViewDividerPosition(String frameIdentifier) {
		try {
			return Integer.parseInt(getValue(LISTVIEW_DIVIDER + frameIdentifier, true));
		} catch (Exception e) {
			return null;
		}
	}

	public synchronized void setQEViewDividerPosition(String frameIdentifier, int value) {
		setValue(QEVIEW_DIVIDER + frameIdentifier, true, String.valueOf(value));
	}

	public synchronized Integer getQEViewDividerPosition(String frameIdentifier) {
		try {
			return Integer.parseInt(getValue(QEVIEW_DIVIDER + frameIdentifier, true));
		} catch (Exception e) {
			return null;
		}
	}

	// **************************************
	// Login preferences
	// **************************************
	public static final String LASTLOGIN_USERNAME = "oncourse.login.last.username";
	public static final String LASTLOGIN_SERVER_HOST = "oncourse.login.last.host";
	public static final String LASTLOGIN_SERVER_PORT = "oncourse.login.last.port";
	public static final String LASTLOGIN_SERVER_ISSSL = "oncourse.login.last.isssl";

	/**
	 * Get the server host name used for the last login of the client on this workstation.
	 * 
	 * @return host name
	 */
	public synchronized static String getLastLoginServerHost() {
		return getFilePreference(LASTLOGIN_SERVER_HOST, "localhost");
	}

	/**
	 * Set the server host name to be used for the next login of the client on this workstation.
	 * 
	 * @param value host name
	 */
	public synchronized static void setLastLoginServerHost(String value) {
		setFilePreference(LASTLOGIN_SERVER_HOST, value);
	}

	/**
	 * Get the server port used for the last login of the client on this workstation.
	 * 
	 * @return TCP port
	 */
	public synchronized static int getLastLoginServerPort() {
		String value = getFilePreference(LASTLOGIN_SERVER_PORT, "8181");
		if (value == null) {
			return 0;
		}
		return Integer.parseInt(value);
	}

	/**
	 * Set the server port to be used for the next login of the client on this workstation.
	 * 
	 * @param value TCP port
	 */
	public synchronized static void setLastLoginServerPort(int value) {
		setFilePreference(LASTLOGIN_SERVER_PORT, Integer.toString(value));
	}

	/**
	 * Returns weather the last connection to the server was a secure one (SSL)
	 */
	public synchronized static Boolean getLastLoginServerIsSsl() {
		String value = getFilePreference(LASTLOGIN_SERVER_ISSSL, "false");
		if (value == null) {
			return false;
		}
		return Boolean.parseBoolean(value);
	}

	public synchronized static void setLastLoginServerIsSsl(Boolean value) {
		setFilePreference(LASTLOGIN_SERVER_ISSSL, String.valueOf(value));
	}

	/**
	 * Get the user name used for the last login of the client on this workstation.
	 * 
	 * @return user name
	 */
	public synchronized static String getLastLoginServerUserName() {
		return getFilePreference(LASTLOGIN_USERNAME, "");
	}

	/**
	 * Set the user name to be used for the next login of the client on this workstation.
	 * 
	 * @param value user name
	 */
	public synchronized static void setLastLoginServerUserName(String value) {
		setFilePreference(LASTLOGIN_USERNAME, value);
	}

	public synchronized static String getSHA1Fingerprint(String host) {
		return getFilePreference(host, "");
	}

	public synchronized static void setSHA1Fingerprint(String host, String value) {
		setFilePreference(host, value);
	}

	// **************************************
	// File destinations
	// **************************************
	public static final String MAILINGLIST_EXPORT_FOLDER = "report.mailingListExport";
	public static final String REPORT_PDF_FOLDER = "report.pdfSaveFolder";
	public static final String REPORT_XLS_FOLDER = "report.xlsSaveFolder";
	public static final String REPORT_IMPORT_FOLDER = "report.jasperImportFolder";
	public static final String EXPORTTEMPLATE_IMPORT_FOLDER = "exporttemplate.xslImportFolder";
	public static final String AVETMISS_EXPORT_PATH = "avetmiss.lastpath";

	public synchronized File getExportMailingListDestination() {
		String dir = getFilePreference(MAILINGLIST_EXPORT_FOLDER, null);
		if (dir == null || dir.length() == 0) {
			dir = System.getProperty("user.dir");
		}
		return new File(dir);
	}

	public synchronized void setExportMailingListDestination(File value) {
		setFilePreference(MAILINGLIST_EXPORT_FOLDER, value.getAbsolutePath());
	}

	public synchronized File getExportPdfDestination() {
		String dir = getFilePreference(REPORT_PDF_FOLDER, null);
		if (dir == null || dir.length() == 0) {
			dir = System.getProperty("user.dir");
		}
		return new File(dir);
	}

	public synchronized void setExportPdfDestination(File value) {
		setFilePreference(REPORT_PDF_FOLDER, value.getAbsolutePath());
	}

	public synchronized File getExportExcelDestination() {
		String dir = getFilePreference(REPORT_XLS_FOLDER, null);
		if (dir == null || dir.length() == 0) {
			dir = System.getProperty("user.dir");
		}
		return new File(dir);
	}

	public synchronized void setExportExcelDestination(File value) {
		setFilePreference(REPORT_XLS_FOLDER, value.getAbsolutePath());
	}

	public synchronized File getReportImportSource() {
		String dir = getFilePreference(REPORT_IMPORT_FOLDER, null);
		if (dir == null || dir.length() == 0) {
			dir = System.getProperty("user.dir");
		}
		return new File(dir);
	}

	public synchronized void setReportImportSource(File value) {
		setFilePreference(REPORT_IMPORT_FOLDER, value.getAbsolutePath());
	}

	public synchronized File getExportTemplateImportSource() {
		String dir = getFilePreference(EXPORTTEMPLATE_IMPORT_FOLDER, null);
		if (dir == null || dir.length() == 0) {
			dir = System.getProperty("user.dir");
		}
		return new File(dir);
	}

	public synchronized void setExportTemplateImportSource(File value) {
		setFilePreference(EXPORTTEMPLATE_IMPORT_FOLDER, value.getAbsolutePath());
	}

	public synchronized File getAvetmissExportPath() {
		String dir = getFilePreference(AVETMISS_EXPORT_PATH, null);
		if (dir == null || dir.length() == 0) {
			dir = System.getProperty("user.dir");
		}
		return new File(dir);
	}

	public synchronized void setAvetmissExportPath(File value) {
		setFilePreference(AVETMISS_EXPORT_PATH, value.getAbsolutePath());
	}

	// **************************************
	// External storage preferences
	// **************************************
	public static final String STORAGE_BUCKET_NAME = "storage.bucket";
	public static final String STORAGE_ACCESS_ID = "storage.access.id";
	public static final String STORAGE_ACCESS_KEY = "storage.access.key";

	public synchronized void setStorageBucketName(String value) {
		setValue(STORAGE_BUCKET_NAME, false, value);
	}

	public synchronized String getStorageBucketName() {
		return getValue(STORAGE_BUCKET_NAME, false);
	}

	public synchronized void setStorageAccessId(String value) {
		setValue(STORAGE_ACCESS_ID, false, value);
	}

	public synchronized String getStorageAccessId() {
		return getValue(STORAGE_ACCESS_ID, false);
	}

	public synchronized void setStorageAccessKey(String value) {
		setValue(STORAGE_ACCESS_KEY, false, value);
	}

	public synchronized String getStorageAccessKey() {
		return getValue(STORAGE_ACCESS_KEY, false);
	}

	public synchronized boolean isUsingExternalStorage() {
		return StringUtils.trimToNull(getStorageAccessId()) != null;
	}

	/*
	 * Other preferences
	 */
	public static final String USE_ONLY_OFFERED_MODULES_AND_QUALIFICATIONS = "use.offered.qualifications.only";
	public static final String MYOB_LAST_EXPORT_DATE = "myob.last.export.date";

	public synchronized boolean getUseOnlyOfferedModulesAndQualifications() {
		return Boolean.parseBoolean(getValue(USE_ONLY_OFFERED_MODULES_AND_QUALIFICATIONS, false));
	}

	public synchronized void setUseOnlyOfferedModulesAndQualifications(boolean value) {
		setValue(USE_ONLY_OFFERED_MODULES_AND_QUALIFICATIONS, false, "" + value);
	}

	public synchronized Date getMYOBLastExportDate() {
		if (getValue(MYOB_LAST_EXPORT_DATE, false) == null) {
			return null;
		}
		try {
			return dateFormat.parse(getValue(MYOB_LAST_EXPORT_DATE, false));
		} catch (ParseException e) {
			return null;
		}
	}

	public synchronized void setMYOBLastExportDate(Date value) {
		setValue(MYOB_LAST_EXPORT_DATE, false, dateFormat.format(value));
	}
	/*
	 * Utility methods
	 */
	public synchronized boolean hasEmailBeenConfigured() {
		String smtpHost = getEmailSMTPHost();
		String fromAddress = getEmailFromAddress();
		if (smtpHost == null || fromAddress == null || smtpHost.length() == 0 || fromAddress.length() == 0) {
			return false;
		}
		return true;
	}

	public synchronized boolean hasSMSBeenConfigured() {
		boolean enableSMSDelivery = getLicenseSms();
		String smsFrom = getSMSFromAddress();
		if (enableSMSDelivery && smsFrom != null && NO_WHITESPACE_PATTERN.matcher(smsFrom).matches()) {
			return true;
		}
		return false;
	}

	/**
	 * Gets value for given key.
	 * 
	 * @param key - the property/attribute key
	 * @return the human readable version
	 */
	public synchronized Object getValueForKey(String key) {

		if (USE_ONLY_OFFERED_MODULES_AND_QUALIFICATIONS.equals(key)) {
			return getUseOnlyOfferedModulesAndQualifications();
		} else if (COLLEGE_NAME.equals(key)) {
			return getCollegeName();
		} else if (COLLEGE_ABN.equals(key)) {
			return getCollegeABN();
		} else if (COLLEGE_URL.equals(key)) {
			return getCollegeURL();
		} else if (SERVICES_LDAP_AUTHENTICATION.equals(key)) {
			return getServicesLdapAuthentication();
		} else if (SERVICES_LDAP_AUTHORISATION.equals(key)) {
			return getServicesLdapAuthorisation();
		} else if (SERVICES_CC_ENABLED.equals(key)) {
			return getServicesCCEnabled();
		} else if (SERVICES_SECURITYKEY.equals(key)) {
			return getServicesSecurityKey();
		} else if (DATA_SVNVERSION.equals(key)) {
			return getDataSVN();
		} else if (DATA_WED_VERSION.equals(key)) {
			return getDataVersion();
		} else if (SERVICES_INFO_REPLICATION_VERSION.equals(key)) {
			return getReferenceDataVersion();
		} else if (DEDUPE_LASTRUN.equals(key)) {
			return getDedupeLastRun();
		} else if (SERVICES_INFO_REPLICATION_LASTRUN.equals(key)) {
			return getInfoReplicationLastRun();
		} else if (EMAIL_SMTPHOST.equals(key)) {
			return getEmailSMTPHost();
		} else if (EMAIL_ADMIN_ADDRESS.equals(key)) {
			return getEmailAdminAddress();
		} else if (EMAIL_FROM_ADDRESS.equals(key)) {
			return getEmailFromAddress();
		} else if (EMAIL_FROM_NAME.equals(key)) {
			return getEmailFromName();
		} else if (EMAIL_BOUNCE_ENABLED.equals(key)) {
			return getEmailBounceEnabled();
		} else if (EMAIL_POP3HOST.equals(key)) {
			return getEmailPOP3Host();
		} else if (EMAIL_BOUNCEADDRESS.equals(key)) {
			return getEmailBounceAddress();
		} else if (EMAIL_POP3ACCOUNT.equals(key)) {
			return getEmailPOP3Account();
		} else if (EMAIL_POP3PASSWORD.equals(key)) {
			return getEmailPOP3Password();
		} else if (SMS_FROM_ADDRESS.equals(key)) {
			return getSMSFromAddress();
		} else if (LDAP_HOST.equals(key)) {
			return getLdapHost();
		} else if (LDAP_DOMAIN.equals(key)) {
			return getLdapDomain();
		} else if (LDAP_SSL.equals(key)) {
			return getLdapSSL();
		} else if (LDAP_SERVERPORT.equals(key)) {
			return getLdapServerport();
		} else if (LDAP_SECURITY.equals(key)) {
			return getLdapSecurity();
		} else if (LDAP_BIND_USER_DN.equals(key)) {
			return getLdapBindUserDn();
		} else if (LDAP_BIND_USER_PASS.equals(key)) {
			return getLdapBindUserPass();
		} else if (LDAP_BASE_DN.equals(key)) {
			return getLdapBaseDN();
		} else if (LDAP_GROUP_SEARCH_FILTER.equals(key)) {
			return getLdapGroupSearchFilter();
		} else if (LDAP_GROUP_ATTRIBUTE.equals(key)) {
			return getLdapGroupAttribute();
		} else if (LDAP_GROUP_MEMBER_ATTRIBUTE.equals(key)) {
			return getLdapGroupMemberAttribute();
		} else if (LDAP_GROUP_POSIX_STYLE.equals(key)) {
			return getLdapGroupPosixStyle();
		} else if (LDAP_USER_SEARCH_FILTER.equals(key)) {
			return getLdapUserSearchFilter();
		} else if (LDAP_USERNAME_ATTRIBUTE.equals(key)) {
			return getLdapUsernameAttribute();
		} else if (LOGOUT_ENABLED.equals(key)) {
			return getLogoutEnabled();
		} else if (LOGOUT_TIMEOUT.equals(key)) {
			return getLogoutTimeout();
		} else if (BACKUP_ENABLED.equals(key)) {
			return getBackupEnabled();
		} else if (BACKUP_DIR.equals(key)) {
			return getBackupDir();
		} else if (BACKUP_DIR_WARNING.equals(key)) {
			return getBackupDirWarning();
		} else if (BACKUP_MAX_HISTORY.equals(key)) {
			return getBackupMaxNumber();
		} else if (BACKUP_NEXT_NUMBER.equals(key)) {
			return getBackupNextNumber();
		} else if (BACKUP_TIMEOFDAY.equals(key)) {
			return getBackupOnMinuteOfDay();
		} else if (ACCOUNT_CURRENCY.equals(key)) {
			return getCountry();
		} else if (ACCOUNT_TAXPK.equals(key)) {
			return getTaxPK();
		} else if (PAY_PERIOD_DAYS.equals(key)) {
			return getPayPeriodDays();
		} else if (AVETMISS_SHOW_GUI.equals(key)) {
			return getShowRTOGUI();
		} else if (AVETMISS_ID.equals(key)) {
			return getAvetmissID();
		} else if (AVETMISS_COLLEGENAME.equals(key)) {
			return getAvetmissCollegeName();
		} else if (AVETMISS_COLLEGESHORTNAME.equals(key)) {
			return getAvetmissCollegeShortName();
		} else if (AVETMISS_TYPE.equals(key)) {
			return getAvetmissType();
		} else if (AVETMISS_ADDRESS1.equals(key)) {
			return getAvetmissAddress1();
		} else if (AVETMISS_ADDRESS2.equals(key)) {
			return getAvetmissAddress2();
		} else if (AVETMISS_SUBURB.equals(key)) {
			return getAvetmissSuburb();
		} else if (AVETMISS_POSTCODE.equals(key)) {
			return getAvetmissPostcode();
		} else if (AVETMISS_STATE.equals(key)) {
			return getAvetmissState();
		} else if (AVETMISS_STATE_NAME_VirtualKey.equals(key)) {
			return getAvetmissStateName();
		} else if (AVETMISS_CONTACTNAME.equals(key)) {
			return getAvetmissContactName();
		} else if (AVETMISS_PHONE.equals(key)) {
			return getAvetmissPhone();
		} else if (AVETMISS_FAX.equals(key)) {
			return getAvetmissFax();
		} else if (AVETMISS_EMAIL.equals(key)) {
			return getAvetmissEmail();
		} else if (AVETMISS_CERT_SIGNATORY_NAME.equals(key)) {
			return getAvetmissCertSignatoryName();
		} else if (DEFAULT_REPORT_OVERLAY.equals(key)) {
			return getDefaultReportOverlay();
		} else if (TOOLBAR_COLLAPSE_STATE.equals(key)) {
			return getToolbarCollapsed();
		} else if (LASTLOGIN_USERNAME.equals(key)) {
			return getLastLoginServerUserName();
		} else if (LASTLOGIN_SERVER_HOST.equals(key)) {
			return getLastLoginServerHost();
		} else if (LASTLOGIN_SERVER_PORT.equals(key)) {
			return getLastLoginServerPort();
		} else if (LASTLOGIN_SERVER_ISSSL.equals(key)) {
			return getLastLoginServerIsSsl();
		} else if (REPORT_PDF_FOLDER.equals(key)) {
			return getExportPdfDestination();
		} else if (REPORT_XLS_FOLDER.equals(key)) {
			return getExportExcelDestination();
		} else if (REPORT_IMPORT_FOLDER.equals(key)) {
			return getReportImportSource();
		} else if (EXPORTTEMPLATE_IMPORT_FOLDER.equals(key)) {
			return getExportTemplateImportSource();
		} else if (AVETMISS_EXPORT_PATH.equals(key)) {
			return getAvetmissExportPath();
		} else if (AVETMISS_JURISDICTION.equals(key)) {
			return getAvetmissJurisdiction();
		} else if (COLLEGE_PAYMENT_INFO.equals(key)) {
			return getPaymentInfo();
		} else if (QE_DEFAULTS_TO_ZERO.equals(key)) {
			return getQEDefaultsToZeroPayment();
		} else if (key.startsWith(EULA_AGREEMENT)) {
			return getEulaAgreement(key);
		} else if (key.equals(TOOLBAR_ACTIVE_TAB)) {
			return getToolbarActiveTab();
		} else if (PORTAL_HIDE_CLASS_ROLL_CONTACT_PHONE.equals(key)) {
			return getPortalHideClassRollContactPhone();
		} else if (PORTAL_HIDE_CLASS_ROLL_CONTACT_EMAIL.equals(key)) {
			return getPortalHideClassRollContactEmail();
		} else if (ACCOUNT_PREPAID_FEES_POST_AT.equals(key)) {
			return getAccountPrepaidFeesPostAt();
		} else if (REPLICATION_ENABLED.equals(key)) {
			return getReplicationEnabled();
		} else if (CLASS_DEFAULTS_DELIVERY_MODE.equals(key)) {
			return getCourseClassDefaultDeliveryMode();
		} else if (CLASS_DEFAULTS_FUNDING_SOURCE.equals(key)) {
			return getCourseClassDefaultFundingSource();
		} else if (CLASS_DEFAULTS_MAXIMUM_PLACES.equals(key)) {
			return getCourseClassDefaultMaximumPlaces();
		} else if (CLASS_DEFAULTS_MINIMUM_PLACES.equals(key)) {
			return getCourseClassDefaultMinimumPlaces();
		} else if (MYOB_LAST_EXPORT_DATE.equals(key)) {
			return getMYOBLastExportDate();
		} else if (QE_DEFAULT_REPORT_INVOICE_KEYCODE.equals(key)) {
			return getDefaultQEInvoiceReportKeycode();
		} else if (QE_DEFAULT_REPORT_ENROLMENT_KEYCODE.equals(key)) {
			return getDefaultQEEnrolmentReportKeycode();
		} else if (GRAVATAR.equals(key)) {
			return getGravatarEnabled();
		}

		if (DEPRECATED_PREFERENCES.contains(key)) {
			logger.info("accessing deprecated preference : " + key);
			return null;
		}

		throw new IllegalArgumentException("Key not found. (" + key + ")");
	}

	public synchronized void setValueForKey(String key, Object value) {

		if (EMAIL_ADMIN_ADDRESS.equals(key) || EMAIL_FROM_ADDRESS.equals(key) || EMAIL_BOUNCEADDRESS.equals(key)) {
			if (((String) value).startsWith("\"") && ((String) value).endsWith("\"")) {
				value = ((String) value).replace('"', ' ');
			}
		}

		if (USE_ONLY_OFFERED_MODULES_AND_QUALIFICATIONS.equals(key)) {
			setUseOnlyOfferedModulesAndQualifications((Boolean) value);
		} else if (COLLEGE_NAME.equals(key)) {
			setCollegeName((String) value);
		} else if (COLLEGE_ABN.equals(key)) {
			setCollegeABN((String) value);
		} else if (COLLEGE_URL.equals(key)) {
			setCollegeURL((String) value);
		} else if (SERVICES_LDAP_AUTHENTICATION.equals(key)) {
			setServicesLdapAuthentication((Boolean) value);
		} else if (SERVICES_LDAP_AUTHORISATION.equals(key)) {
			setServicesLdapAuthorisation((Boolean) value);
		} else if (SERVICES_CC_ENABLED.equals(key)) {
			setServicesCCEnabled((Boolean) value);
		} else if (SERVICES_SECURITYKEY.equals(key)) {
			setServicesSecurityKey((String) value);
		} else if (DATA_SVNVERSION.equals(key)) {
			setDataSVN((Integer) value);
		} else if (DATA_WED_VERSION.equals(key)) {
			setDataVersion((String) value);
		} else if (SERVICES_INFO_REPLICATION_VERSION.equals(key)) {
			setReferenceDataVersion((Integer) value);
		} else if (DEDUPE_LASTRUN.equals(key)) {
			setDedupeLastRun((Date) value);
		} else if (SERVICES_INFO_REPLICATION_LASTRUN.equals(key)) {
			setInfoReplicationLastRun((Date) value);
		} else if (EMAIL_SMTPHOST.equals(key)) {
			setEmailSMTPHost((String) value);
		} else if (EMAIL_ADMIN_ADDRESS.equals(key)) {
			setEmailAdminAddress((String) value);
		} else if (EMAIL_FROM_ADDRESS.equals(key)) {
			setEmailFromAddress((String) value);
		} else if (EMAIL_FROM_NAME.equals(key)) {
			setEmailFromName((String) value);
		} else if (EMAIL_BOUNCE_ENABLED.equals(key)) {
			setEmailBounceEnabled((Boolean) value);
		} else if (EMAIL_POP3HOST.equals(key)) {
			setEmailPOP3Host((String) value);
		} else if (EMAIL_BOUNCEADDRESS.equals(key)) {
			setEmailBounceAddress((String) value);
		} else if (EMAIL_POP3ACCOUNT.equals(key)) {
			setEmailPOP3Account((String) value);
		} else if (EMAIL_POP3PASSWORD.equals(key)) {
			setEmailPOP3Password((String) value);
		} else if (SMS_FROM_ADDRESS.equals(key)) {
			setSMSFromAddress((String) value);
		} else if (LDAP_HOST.equals(key)) {
			setLdapHost((String) value);
		} else if (LDAP_DOMAIN.equals(key)) {
			setLdapDomain((String) value);
		} else if (LDAP_SERVERPORT.equals(key)) {
			setLdapServerport((Integer) value);
		} else if (LDAP_SSL.equals(key)) {
			setLdapSSL((Boolean) value);
		} else if (LDAP_SECURITY.equals(key)) {
			setLdapSecurity((String) value);
		} else if (LDAP_BIND_USER_DN.equals(key)) {
			setLdapBindUserDn((String) value);
		} else if (LDAP_BIND_USER_PASS.equals(key)) {
			setLdapBindUserPass((String) value);
		} else if (LDAP_BASE_DN.equals(key)) {
			setLdapBaseDN((String) value);
		} else if (LDAP_GROUP_SEARCH_FILTER.equals(key)) {
			setLdapGroupSearchFilter((String) value);
		} else if (LDAP_GROUP_ATTRIBUTE.equals(key)) {
			setLdapGroupAttribute((String) value);
		} else if (LDAP_GROUP_MEMBER_ATTRIBUTE.equals(key)) {
			setLdapGroupMemberAttribute((String) value);
		} else if (LDAP_GROUP_POSIX_STYLE.equals(key)) {
			setLdapGroupPosixStyle((Boolean) value);
		} else if (LDAP_USER_SEARCH_FILTER.equals(key)) {
			setLdapUserSearchFilter((String) value);
		} else if (LDAP_USERNAME_ATTRIBUTE.equals(key)) {
			setLdapUsernameAttribute((String) value);
		} else if (LOGOUT_ENABLED.equals(key)) {
			setLogoutEnabled((Boolean) value);
		} else if (LOGOUT_TIMEOUT.equals(key)) {
			setLogoutTimeout((String) value);
		} else if (BACKUP_ENABLED.equals(key)) {
			setBackupEnabled((Boolean) value);
		} else if (BACKUP_DIR.equals(key)) {
			setBackupDir((String) value);
		} else if (BACKUP_DIR_WARNING.equals(key)) {
			setBackupDirWarning((String) value);
		} else if (BACKUP_MAX_HISTORY.equals(key)) {
			setBackupMaxNumber((Integer) value);
		} else if (BACKUP_NEXT_NUMBER.equals(key)) {
			setBackupNextNumber((Integer) value);
		} else if (BACKUP_TIMEOFDAY.equals(key)) {
			setBackupOnMinuteOfDay((Integer) value);
		} else if (ACCOUNT_CURRENCY.equals(key)) {
			setCountry((Country) value);
		} else if (ACCOUNT_TAXPK.equals(key)) {
			setTaxPK((Long) value);
		} else if (PAY_PERIOD_DAYS.equals(key)) {
			setPayPeriodDays((Integer) value);
		} else if (AVETMISS_SHOW_GUI.equals(key)) {
			setShowRTOGUI((Boolean) value);
		} else if (AVETMISS_ID.equals(key)) {
			setAvetmissID((String) value);
		} else if (AVETMISS_COLLEGENAME.equals(key)) {
			setAvetmissCollegeName((String) value);
		} else if (AVETMISS_COLLEGESHORTNAME.equals(key)) {
			setAvetmissCollegeShortName((String) value);
		} else if (AVETMISS_TYPE.equals(key)) {
			setAvetmissType((String) value);
		} else if (AVETMISS_ADDRESS1.equals(key)) {
			setAvetmissAddress1((String) value);
		} else if (AVETMISS_ADDRESS2.equals(key)) {
			setAvetmissAddress2((String) value);
		} else if (AVETMISS_SUBURB.equals(key)) {
			setAvetmissSuburb((String) value);
		} else if (AVETMISS_POSTCODE.equals(key)) {
			setAvetmissPostcode((String) value);
		} else if (AVETMISS_STATE.equals(key)) {
			setAvetmissState((String) value);
		} else if (AVETMISS_CONTACTNAME.equals(key)) {
			setAvetmissContactName((String) value);
		} else if (AVETMISS_PHONE.equals(key)) {
			setAvetmissPhone((String) value);
		} else if (AVETMISS_FAX.equals(key)) {
			setAvetmissFax((String) value);
		} else if (AVETMISS_EMAIL.equals(key)) {
			setAvetmissEmail((String) value);
		} else if (AVETMISS_CERT_SIGNATORY_NAME.equals(key)) {
			setAvetmissCertSignatoryName((String) value);
		} else if (DEFAULT_REPORT_OVERLAY.equals(key)) {
			setDefaultReportOverlay((Long) value);
		} else if (TOOLBAR_COLLAPSE_STATE.equals(key)) {
			setToolbarCollapsed((Boolean) value);
		} else if (LASTLOGIN_USERNAME.equals(key)) {
			setLastLoginServerUserName((String) value);
		} else if (LASTLOGIN_SERVER_HOST.equals(key)) {
			setLastLoginServerHost((String) value);
		} else if (LASTLOGIN_SERVER_PORT.equals(key)) {
			setLastLoginServerPort((Integer) value);
		} else if (LASTLOGIN_SERVER_ISSSL.equals(key)) {
			setLastLoginServerIsSsl((Boolean) value);
		} else if (REPORT_PDF_FOLDER.equals(key)) {
			setExportPdfDestination((File) value);
		} else if (REPORT_XLS_FOLDER.equals(key)) {
			setExportExcelDestination((File) value);
		} else if (REPORT_IMPORT_FOLDER.equals(key)) {
			setReportImportSource((File) value);
		} else if (EXPORTTEMPLATE_IMPORT_FOLDER.equals(key)) {
			setExportTemplateImportSource((File) value);
		} else if (AVETMISS_EXPORT_PATH.equals(key)) {
			setAvetmissExportPath((File) value);
		} else if (AVETMISS_JURISDICTION.equals(key)) {
			setAvetmissJurisdiction((ExportJurisdiction) value);
		} else if (QE_DEFAULTS_TO_ZERO.equals(key)) {
			setQEDefaultsToZeroPayment((Boolean) value);
		} else if (key.startsWith(EULA_AGREEMENT)) {
			setEulaAgreement(key, (String) value);
		} else if (key.equals(TOOLBAR_ACTIVE_TAB)) {
			setToolbarActiveTab((String) value);
		} else if (key.equals(PORTAL_HIDE_CLASS_ROLL_CONTACT_PHONE)) {
			setPortalHideClassRollContactPhone((String) value);
		} else if (key.equals(PORTAL_HIDE_CLASS_ROLL_CONTACT_EMAIL)) {
			setPortalHideClassRollContactEmail((String) value);
		} else if (ACCOUNT_PREPAID_FEES_POST_AT.equals(key)) {
			setAccountPrepaidFeesPostAt((String) value);
		} else if (CLASS_DEFAULTS_DELIVERY_MODE.equals(key)) {
			setCourseClassDefaultDeliveryMode((DeliveryMode) value);
		} else if (CLASS_DEFAULTS_FUNDING_SOURCE.equals(key)) {
			setCourseClassDefaultFundingSource((ClassFundingSource) value);
		} else if (CLASS_DEFAULTS_MAXIMUM_PLACES.equals(key)) {
			setCourseClassDefaultMaximumPlaces((Integer) value);
		} else if (CLASS_DEFAULTS_MINIMUM_PLACES.equals(key)) {
			setCourseClassDefaultMinimumPlaces((Integer) value);
		} else if (MYOB_LAST_EXPORT_DATE.equals(key)) {
			setMYOBLastExportDate((Date) value);
		} else if (QE_DEFAULT_REPORT_INVOICE_KEYCODE.equals(key)) {
			setDefaultQEInvoiceReportKeycode((String)value);
		} else if (QE_DEFAULT_REPORT_ENROLMENT_KEYCODE.equals(key)) {
			setDefaultQEEnrolmentReportKeycode((String)value);
		} 
	}

	/**
	 * V4 replication.
	 */

	public static final String SERVICES_COMMUNICATION_KEY = "services.soap.communication.key";
	public static final String REPLICATION_ENABLED = "replication.enabled";

	/**
	 * Communication key using to track college sessions during replication to prevent copy of data being started to replicate.
	 * 
	 * @return communication key
	 */
	public synchronized Long getCommunicationKey() {
		String value = getValue(SERVICES_COMMUNICATION_KEY, false);

		if (value == null) {
			Random randomGen = new Random();
			long newCommunicationKey = ((long) randomGen.nextInt(63) << 59) + System.currentTimeMillis();
			setCommunicationKey(newCommunicationKey);
			return newCommunicationKey;
		}

		return Long.valueOf(value);
	}

	/**
	 * Sets communicaiton key to being used during the next replication session.
	 * 
	 * @param communicationKey
	 */
	public synchronized void setCommunicationKey(Long communicationKey) {
		setValue(SERVICES_COMMUNICATION_KEY, false, String.valueOf(communicationKey));
	}

	/**
	 * Shows if replication should be performed for college.
	 */
	public synchronized boolean getReplicationEnabled() {
		return Boolean.parseBoolean(getValue(REPLICATION_ENABLED, false));
	}

	/**
	 * Enabled or disable replication. Should be used only in unit tests.
	 * 
	 * @param value
	 */
	public synchronized void setReplicationEnabled(boolean value) {
		setValue(REPLICATION_ENABLED, false, Boolean.toString(value));
	}

	public static final String FEATURE_CONCESSIONS_IN_ENROLMENT = "feature.concessionsInEnrolment";
	public static final String FEATURE_CONCESSION_USERS_CREATE = "feature.concession.existing.users.create";
	public static final String FEATURE_ENROLMENT_DISCLOSURE = "feature.enrolmentDisclosure";

	public synchronized boolean getFeatureConcessionsInEnrolment() {
		return Boolean.parseBoolean(getValue(FEATURE_CONCESSIONS_IN_ENROLMENT, false));
	}

	public synchronized void setFeatureConcessionsInEnrolment(boolean value) {
		setValue(FEATURE_CONCESSIONS_IN_ENROLMENT, false, Boolean.toString(value));
	}

	public synchronized void setFeatureConcessionsUsersCreate(boolean value) {
		setValue(FEATURE_CONCESSION_USERS_CREATE, false, Boolean.toString(value));
	}

	public synchronized boolean getFeatureConcessionsUsersCreate() {
		return Boolean.parseBoolean(getValue(FEATURE_CONCESSION_USERS_CREATE, false));
	}

	public synchronized String getFeatureEnrolmentDisclosure() {
		return getValue(FEATURE_ENROLMENT_DISCLOSURE, false);
	}

	public synchronized void setFeatureEnrolmentDisclosure(String value) {
		setValue(FEATURE_ENROLMENT_DISCLOSURE, false, value);
	}

	// **************************************
	// Internal mechanics: the stuff that makes it work
	// **************************************

	/**
	 * Retrieve a preference value.
	 * 
	 * @param isUserPref true if specific preference for the currently logged in user
	 * @param key property key
	 * @return String value of preference
	 */
	protected abstract String getValue(String key, boolean isUserPref);

	/**
	 * Set a preference value.
	 * 
	 * @param isUserPref true if specific preference for the currently logged in user
	 * @param key property key
	 * @param value value of the preference
	 */
	protected abstract void setValue(String key, boolean isUserPref, String value);

	/**
	 * Convenience method to deserialize byte[] to Object
	 * 
	 * @param data
	 * @return the deserialized object
	 */
	public synchronized static Object deserializeObject(byte[] data) {
		Object object = null;
		if (data != null && data.length > 0) {
			try {
				ByteArrayInputStream in = new ByteArrayInputStream(data);
				ObjectInputStream s = new ObjectInputStream(in);
				object = s.readObject();
				s.close();
				in.close();
			} catch (Exception e) {
				throw new IllegalStateException("Error while deserializing object", e);
			}
		}
		return object;
	}

	/**
	 * Convenience method to serialize Object to byte[]
	 * 
	 * @param object
	 * @return the bytes representing the serialized object
	 */
	public synchronized static byte[] serializeObject(Object object) {
		byte[] data = null;
		logger.debug("serializeObject:" + object);
		if (object != null) {
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				ObjectOutputStream s = new ObjectOutputStream(out);
				s.writeObject(object);
				s.flush();
				s.close();

				data = out.toByteArray();
			} catch (Exception e) {
				throw new IllegalStateException("Error while serializing the object", e);
			}
		}
		logger.debug("serializeObject result:" + (data == null ? "null" : "length=" + data.length));
		return data;
	}

	public static Map<String, CreditCardType> getCCAvailableTypes(CommonPreferenceController pref) {
		LinkedHashMap<String, CreditCardType> map = new LinkedHashMap<>();
		for (CreditCardType t : CreditCardType.values()) {
			if (!CreditCardType.BANKCARD.equals(t) && (!CreditCardType.AMEX.equals(t) || CreditCardType.AMEX.equals(t) && pref.getServicesAmexEnabled())) {
				map.put(t.getDisplayName(), t);
			}
		}
		return map;
	}

	/**
	 * @param key
	 * @param valueIfNull
	 * @return the preference stored on local disk as string
	 */
	private synchronized static String getFilePreference(String key, String valueIfNull) {
		return FILE_PREFS.get(key, valueIfNull);
	}

	/**
	 * Sets a user's system preference value for the given key
	 * 
	 * @param key
	 * @param value
	 */
	private synchronized static void setFilePreference(String key, String value) {
		FILE_PREFS.put(key, value);
	}

	public synchronized static CommonPreferenceController getController() {
		return sharedController;
	}

	// **************************************
	// portal
	// **************************************

	public static final String PORTAL_HIDE_CLASS_ROLL_CONTACT_PHONE = "portal.hideClassRollContactPhone";
	public static final String PORTAL_HIDE_CLASS_ROLL_CONTACT_EMAIL = "portal.hideClassRollContactEmail";

	public synchronized String getPortalHideClassRollContactPhone() {
		return getValue(PORTAL_HIDE_CLASS_ROLL_CONTACT_PHONE, false);
	}

	public synchronized void setPortalHideClassRollContactPhone(String value) {
		setValue(PORTAL_HIDE_CLASS_ROLL_CONTACT_PHONE, false, value);
	}

	public synchronized String getPortalHideClassRollContactEmail() {
		return getValue(PORTAL_HIDE_CLASS_ROLL_CONTACT_EMAIL, false);
	}

	public synchronized void setPortalHideClassRollContactEmail(String value) {
		setValue(PORTAL_HIDE_CLASS_ROLL_CONTACT_EMAIL, false, value);
	}
}
