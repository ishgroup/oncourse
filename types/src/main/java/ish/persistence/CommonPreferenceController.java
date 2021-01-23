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

import ish.common.types.*;
import ish.math.Country;
import ish.oncourse.common.ExportJurisdiction;
import ish.util.Maps;
import ish.util.SecurityUtil;
import ish.validation.PreferenceValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.*;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

import static ish.persistence.Preferences.*;
/**
 * This abstract class is implemented in each of the client and the server. This is needed because the persistent object classes are different in those two
 * places and access to them needs to be implemented separately.
 */
public abstract class CommonPreferenceController {

	private static final Logger logger = LogManager.getLogger();

	private static final java.util.prefs.Preferences FILE_PREFS = java.util.prefs.Preferences.userNodeForPackage(CommonPreferenceController.class);
	public static final Pattern NO_WHITESPACE_PATTERN = Pattern.compile("^[^\\s]+$");
	public static final Pattern WRAPPING_QUOTATION_MARKS =  Pattern.compile("^[\\\"].*[\\\"]$");

	// use US locale so months in English
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.US);
	private static final Pattern commaExplode = Pattern.compile(",");
	private static final Pattern colonExplode = Pattern.compile(":");

	private static final List<String> DEPRECATED_PREFERENCES = new ArrayList<>();

	static {
		DEPRECATED_PREFERENCES.add("print.header");
	}

	protected static CommonPreferenceController sharedController;

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
	public String getCollegeName() {
		return getValue(COLLEGE_NAME, false);
	}

	/**
	 * Set a name for this college.
	 *
	 * @param value college name
	 */
	public void setCollegeName(String value) {
		setValue(COLLEGE_NAME, false, value);
	}

	/**
	 * Get the college ABN (Australian Business Number)
	 *
	 * @return ABN
	 */
	public String getCollegeABN() {
		return getValue(COLLEGE_ABN, false);
	}

	/**
	 * Get the college USI SOFTWARE ID
	 * @return USI_SOFTWARE_ID
	 */
	public String getUsiSoftwareId() {
		return getValue(USI_SOFTWARE_ID, false);
	}


	/**
	 * Set the college ABN
	 *
	 * @param value ABN
	 */
	public void setCollegeABN(String value) {
		setValue(COLLEGE_ABN, false, value);
	}

	/**
	 * Get the web site URL for this college. Does not include http://
	 *
	 * @return URL string
	 */
	public String getCollegeURL() {
		return getValue(COLLEGE_URL, false);
	}

	/**
	 * Set the web site URL for this college. Do not include http://
	 *
	 * @param value URL string
	 */
	public void setCollegeURL(String value) {
		setValue(COLLEGE_URL, false, value);
	}

	/**
	 * Get the description to put on invoice and help to pay
	 *
	 * @return URL string
	 */
	public String getPaymentInfo() {
		return getValue(COLLEGE_PAYMENT_INFO, false);
	}

	/**
	 * Set the description to put on invoice and help to pay
	 *
	 * @param value URL string
	 */
	public void setPaymentInfo(String value) {
		setValue(COLLEGE_PAYMENT_INFO, false, value);
	}

	/**
	 * Gets the url to redirect after the successful enrolment.
	 *
	 * @return URL string
	 */
	public String getEnrolSuccessUrl() {
		return getValue(COLLEGE_ENROL_SUCCESS_URL, false);
	}


	public  String getPaymentGatewayPass() {
		String apiKey = getValue(PAYMENT_GATEWAY_PASS, false);
		if (apiKey == null) {
			throw new IllegalArgumentException();
		}
		return apiKey;
	}

	public Boolean isPurchaseWithoutAuth() {
		return Boolean.parseBoolean(getValue(PAYMENT_GATEWAY_PURCHASE_WITHOUT_AUTH, false));
	}
	/**
	 * Sets the url to redirect after the successful enrolment.
	 *
	 * @param value URL string
	 */
	public void setEnrolSuccessUrl(String value) {
		setValue(COLLEGE_ENROL_SUCCESS_URL, false, value);
	}

	/**
	 * @return the timezone ID or null
	 */
	public String getOncourseServerDefaultTimezone() {
		return getValue(ONCOURSE_SERVER_DEFAULT_TZ, false);
	}

	/**
	 * @param timezoneID the timezone ID to set as the default timezone for onCourse
	 */
	public void setOncourseServerDefaultTimezone(String timezoneID) {
		setValue(ONCOURSE_SERVER_DEFAULT_TZ, false, timezoneID);
	}

	public boolean getServicesLdapAuthentication() {
		return Boolean.parseBoolean(getValue(SERVICES_LDAP_AUTHENTICATION, false));
	}

	public void setServicesLdapAuthentication(boolean value) {
		setValue(SERVICES_LDAP_AUTHENTICATION, false, Boolean.toString(value));
	}

	public boolean getServicesLdapAuthorisation() {
		return Boolean.parseBoolean(getValue(SERVICES_LDAP_AUTHORISATION, false));
	}

	public void setServicesLdapAuthorisation(boolean value) {
		setValue(SERVICES_LDAP_AUTHORISATION, false, Boolean.toString(value));
	}

	public boolean getServicesCCEnabled() {
		return Boolean.parseBoolean(getValue(SERVICES_CC_ENABLED, false));
	}

	public void setServicesCCEnabled(boolean value) {
		setValue(SERVICES_CC_ENABLED, false, Boolean.toString(value));
	}

	public boolean getServicesAmexEnabled() {
		return Boolean.parseBoolean(getValue(SERVICES_CC_AMEX_ENABLED, false));
	}

	public BigInteger getReplicationRequeueId() {
		String value = getValue(SERVICES_REPLICATION_REQUEUE_ID, false);
		return value == null ? null : new BigInteger(value);
	}

	public void setReplicationRequeueId(BigInteger value) {
		setValue(SERVICES_REPLICATION_REQUEUE_ID, false, value.toString());
	}

	public int getDataSVN() {
		String result = getValue(DATA_SVNVERSION, false);
		if (result == null) {
			return -1;
		}
		return Integer.parseInt(result);
	}

	public void setDataSVN(int value) {
		setValue(DATA_SVNVERSION, false, Integer.toString(value));
	}

	/** this is not a wed version anymore, but I dont wanna change the preferences. marcin **/
	public String getDataVersion() {
		return getValue(DATA_WED_VERSION, false);
	}

	public void setDataVersion(String value) {
		setValue(DATA_WED_VERSION, false, value);
	}

	public Long getReferenceDataVersion() {
		String result = getValue(SERVICES_INFO_REPLICATION_VERSION, false);
		if (result == null) {
			return -1L;
		}
		return Long.parseLong(result);
	}

	public void setReferenceDataVersion(long value) {
		setValue(SERVICES_INFO_REPLICATION_VERSION, false, Long.toString(value));
	}

	public Date getDedupeLastRun() {
		try {
			return dateFormat.parse(getValue(DEDUPE_LASTRUN, false));
		} catch (ParseException e) {
			return null;
		}
	}

	public void setDedupeLastRun(Date value) {
		setValue(DEDUPE_LASTRUN, false, dateFormat.format(value));
	}

	public Date getInfoReplicationLastRun() {
		if (getValue(SERVICES_ANGEL_REPLICATION_LASTRUN, false) == null) {
			return null;
		}
		try {
			return dateFormat.parse(getValue(SERVICES_INFO_REPLICATION_LASTRUN, false));
		} catch (ParseException e) {
			return null;
		}
	}

	public void setInfoReplicationLastRun(Date value) {
		setValue(SERVICES_INFO_REPLICATION_LASTRUN, false, dateFormat.format(value));
	}

	public Date getAngelReplicationLastRun() {
		if (getValue(SERVICES_ANGEL_REPLICATION_LASTRUN, false) == null) {
			return null;
		}
		try {
			return dateFormat.parse(getValue(SERVICES_ANGEL_REPLICATION_LASTRUN, false));
		} catch (ParseException e) {
			return null;
		}
	}

	public void setAngelReplicationLastRun(Date value) {
		setValue(SERVICES_ANGEL_REPLICATION_LASTRUN, false, dateFormat.format(value));
	}

	public static boolean LICENSE_BYPASS_MODE = false;

	public boolean getLicenseAccessControl() {
		return LICENSE_BYPASS_MODE || Boolean.parseBoolean(getValue(LICENSE_ACCESS_CONTROL, false));
	}

	public void setLicenseAccessControl(boolean value) {
		throw new IllegalStateException("Licences must replicate from ish");
// setValue(LICENSE_ACCESS_CONTROL, false, Boolean.toString(value));
	}

	public boolean getLicenseLdap() {
		return LICENSE_BYPASS_MODE || Boolean.parseBoolean(getValue(LICENSE_LDAP, false));
	}

	public void setLicenseLdap(boolean value) {
		throw new IllegalStateException("Licences must replicate from ish");
// setValue(LICENSE_LDAP, false, Boolean.toString(value));
	}

	public boolean getLicenseBudget() {
		return LICENSE_BYPASS_MODE || Boolean.parseBoolean(getValue(LICENSE_BUDGET, false));
	}

	public void setLicenseBudget(boolean value) {
		throw new IllegalStateException("Licences must replicate from ish");
// setValue(LICENSE_BUDGET, false, Boolean.toString(value));
	}

	public boolean getLicenseExternalDB() {
		return LICENSE_BYPASS_MODE || Boolean.parseBoolean(getValue(LICENSE_EXTENRNAL_DB, false));
	}

	public void setLicenseExternalDB(boolean value) {
		throw new IllegalStateException("Licences must replicate from ish");
// setValue(LICENSE_EXTENRNAL_DB, false, Boolean.toString(value));
	}

	/**
	 * @deprecated - not used since angel 4.1
	 */
	@Deprecated
	public boolean getLicenseSSL() {
		return LICENSE_BYPASS_MODE || Boolean.parseBoolean(getValue(LICENSE_SSL, false));
	}

	/**
	 * @deprecated - not used since angel 4.1
	 */
	@Deprecated
	public void setLicenseSSL(boolean value) {
		throw new IllegalStateException("Licences must replicate from ish");
// setValue(LICENSE_SSL, false, Boolean.toString(value));
	}

	public boolean getLicenseSms() {
		return Boolean.parseBoolean(getValue(LICENSE_SMS, false));
	}

	public void setLicenseSms(boolean value) {
		throw new IllegalStateException("Licences must replicate from ish");
// setValue(LICENSE_SMS, false, Boolean.toString(value));
	}

	public boolean getLicenseCCProcessing() {
		return Boolean.parseBoolean(getValue(LICENSE_CC_PROCESSING, false));
	}

	public void setLicenseCCProcessing(boolean value) {
		throw new IllegalStateException("Licences must replicate from ish");
// setValue(LICENSE_CC_PROCESSING, false, Boolean.toString(value));
	}

	public boolean getLicensePayroll() {
		return LICENSE_BYPASS_MODE || Boolean.parseBoolean(getValue(LICENSE_PAYROLL, false));
	}

	public void setLicensePayroll(boolean value) {
		throw new IllegalStateException("Licences must replicate from ish");
// setValue(LICENSE_WEBSITE, false, Boolean.toString(value));
	}

	public boolean getLicenseVoucher() {
		return LICENSE_BYPASS_MODE || Boolean.parseBoolean(getValue(LICENSE_VOUCHER, false));
	}

	public void setLicenseVoucher(boolean value) {
		throw new IllegalStateException("Licences must replicate from ish");
	}

	public boolean getLicenseMembership() {
		return LICENSE_BYPASS_MODE || Boolean.parseBoolean(getValue(LICENSE_MEMBERSHIP, false));
	}

	public void setLicenseMembership(boolean value) {
		throw new IllegalStateException("Licences must replicate from ish");
	}

	public boolean getLicenseAttendance() {
		return LICENSE_BYPASS_MODE || Boolean.parseBoolean(getValue(LICENSE_ATTENDANCE, false));
	}

	public void setLicenseAttendance(boolean value) {
		throw new IllegalStateException("Licences must replicate from ish");
	}

	public boolean getLicenseScripting() {
		return LICENSE_BYPASS_MODE || Boolean.parseBoolean(getValue(LICENSE_SCRIPTING, false));
	}

	public boolean getLicenseFeeHelpExport () {
		return LICENSE_BYPASS_MODE || Boolean.parseBoolean(getValue(LICENSE_FEE_HELP_EXPORT, false));
	}

	public void setLicenseScripting(boolean value) {
		throw new IllegalStateException("Licences must replicate from ish");
	}

	public boolean getLicenseFundingContract() {
		return LICENSE_BYPASS_MODE || Boolean.parseBoolean(getValue(LICENSE_FUNDING_CONTRACT, false));
	}

	public void setLicenseFundingContract(boolean value) {
		throw new IllegalStateException("Licences must replicate from ish");
	}

	public boolean getLicenseAutopay() {
		return LICENSE_BYPASS_MODE || Boolean.parseBoolean(getValue(LICENSE_AUTO_PAY, false));
	}

	public void setLicenseAutopay(boolean value) {
		throw new IllegalStateException("Licences must replicate from ish");
	}

	public String getEmailAdminAddress() {
		return getValue(EMAIL_ADMIN_ADDRESS, false);
	}

	public void setEmailAdminAddress(String value) {
		setValue(EMAIL_ADMIN_ADDRESS, false, value);
	}

	public String getEmailFromAddress() {
		return getValue(EMAIL_FROM_ADDRESS, false);
	}

	public void setEmailFromAddress(String value) {
		setValue(EMAIL_FROM_ADDRESS, false, value);
	}

	public String getEmailFromName() {
		return getValue(EMAIL_FROM_NAME, false);
	}

	public void setEmailFromName(String value) {
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

	public void setEmailBounceEnabled(boolean value) {
		setValue(EMAIL_BOUNCE_ENABLED, false, Boolean.toString(value));
	}

	public String getEmailPOP3Host() {
		return getValue(EMAIL_POP3HOST, false);
	}

	public void setEmailPOP3Host(String value) {
		setValue(EMAIL_POP3HOST, false, value);
	}

	public String getEmailBounceAddress() {
		return getValue(EMAIL_BOUNCEADDRESS, false);
	}

	public void setEmailBounceAddress(String value) {
		setValue(EMAIL_BOUNCEADDRESS, false, value);
	}

	public String getEmailPOP3Account() {
		return getValue(EMAIL_POP3ACCOUNT, false);
	}

	public void setEmailPOP3Account(String value) {
		setValue(EMAIL_POP3ACCOUNT, false, value);
	}

	public String getEmailPOP3Password() {
		return getValue(EMAIL_POP3PASSWORD, false);
	}

	public void setEmailPOP3Password(String value) {
		setValue(EMAIL_POP3PASSWORD, false, value);
	}

	public String getSMSFromAddress() {
		return getValue(SMS_FROM_ADDRESS, false);
	}

	public void setSMSFromAddress(String value) {
		setValue(SMS_FROM_ADDRESS, false, value);
	}

	public static final Map<String, String> Ldap_SecurityTypes = Maps.asLinkedMap(new String[] {
			"Simple Authentication (unencrypted connection)",
			"SASL Authentication (secure connection)" }, new String[] { LDAP_SIMPLE_AUTHENTICATION, LDAP_SASL_AUTHENTICATION });

	public String getLdapHost() {
		return getValue(LDAP_HOST, false);
	}

	public void setLdapHost(String value) {
		setValue(LDAP_HOST, false, value);
	}

	@Deprecated
	public String getLdapDomain() {
		return getValue(LDAP_DOMAIN, false);
	}

	@Deprecated
	public void setLdapDomain(String value) {
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

	public void setLdapServerport(int value) {
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
	public void setLdapSecurity(String value) {
		setValue(LDAP_SECURITY, false, value);
	}

	public String getLdapBindUserDn() {
		return getValue(LDAP_BIND_USER_DN, false);
	}

	public void setLdapBindUserDn(String value) {
		setValue(LDAP_BIND_USER_DN, false, value);
	}

	public String getLdapBindUserPass() {
		return getValue(LDAP_BIND_USER_PASS, false);
	}

	public void setLdapBindUserPass(String value) {
		setValue(LDAP_BIND_USER_PASS, false, value);
	}

	public Boolean getLdapSSL() {
		try {
			return Boolean.parseBoolean(getValue(LDAP_SSL, false));
		} catch (Exception e) {
			return false;
		}
	}

	public void setLdapSSL(Boolean value) {
		setValue(LDAP_SSL, false, String.valueOf(value));
	}

	public String getLdapBaseDN() {
		return getValue(LDAP_BASE_DN, false);
	}

	public void setLdapBaseDN(String value) {
		setValue(LDAP_BASE_DN, false, value);
	}

	public String getLdapGroupSearchFilter() {
		return getValue(LDAP_GROUP_SEARCH_FILTER, false);
	}

	public void setLdapGroupSearchFilter(String value) {
		setValue(LDAP_GROUP_SEARCH_FILTER, false, value);
	}

	public String getLdapGroupAttribute() {
		return getValue(LDAP_GROUP_ATTRIBUTE, false);
	}

	public void setLdapGroupAttribute(String value) {
		setValue(LDAP_GROUP_ATTRIBUTE, false, value);
	}

	public String getLdapGroupMemberAttribute() {
		return getValue(LDAP_GROUP_MEMBER_ATTRIBUTE, false);
	}

	public void setLdapGroupMemberAttribute(String value) {
		setValue(LDAP_GROUP_MEMBER_ATTRIBUTE, false, value);
	}

	public Boolean getLdapGroupPosixStyle() {
		try {
			return Boolean.parseBoolean(getValue(LDAP_GROUP_POSIX_STYLE, false));
		} catch (Exception e) {
			return false;
		}
	}

	public void setLdapGroupPosixStyle(Boolean value) {
		setValue(LDAP_GROUP_POSIX_STYLE, false, String.valueOf(value));
	}

	public String getLdapUserSearchFilter() {
		return getValue(LDAP_USER_SEARCH_FILTER, false);
	}

	public void setLdapUserSearchFilter(String value) {
		setValue(LDAP_USER_SEARCH_FILTER, false, value);
	}

	public String getLdapUsernameAttribute() {
		return getValue(LDAP_USERNAME_ATTRIBUTE, false);
	}

	public void setLdapUsernameAttribute(String value) {
		setValue(LDAP_USERNAME_ATTRIBUTE, false, value);
	}

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

	public void setBackupEnabled(boolean value) {
		setValue(BACKUP_ENABLED, false, Boolean.toString(value));
	}

	public Integer getBackupOnMinuteOfDay() {
		try {
			return Integer.parseInt(getValue(BACKUP_TIMEOFDAY, false));
		} catch (Exception e) {
			return null;
		}
	}

	public void setBackupOnMinuteOfDay(int value) {
		setValue(BACKUP_TIMEOFDAY, false, String.valueOf(value));
	}

	public boolean getLogoutEnabled() {
		String aPref = getValue(LOGOUT_ENABLED, false);
		if (aPref == null) {
			return false;
		}
		return Boolean.parseBoolean(aPref);

	}

	public void setLogoutEnabled(boolean value) {
		setValue(LOGOUT_ENABLED, false, Boolean.toString(value));
	}

	/**
	 * How long before the client logs out in seconds.
	 *
	 * @return seconds before logout due to inactivity
	 */
	public long getLogoutTimeout() {
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

	public void setLogoutTimeout(String value) {
		setValue(LOGOUT_TIMEOUT, false, value);
	}

	public String getBackupDir() {
		return getValue(BACKUP_DIR, false);
	}

	public synchronized void setBackupDir(String value) {
		setValue(BACKUP_DIR, false, value);
		setBackupDirWarning(""); // clear warning message
	}

	public String getBackupDirWarning() {
		return getValue(BACKUP_DIR_WARNING, false);
	}

	public void setBackupDirWarning(String message) {
		setValue(BACKUP_DIR_WARNING, false, message);
	}

	public Integer getBackupNextNumber() {
		try {
			return Integer.parseInt(getValue(BACKUP_NEXT_NUMBER, false));
		} catch (Exception e) {
			return null;
		}
	}

	public void setBackupNextNumber(int value) {
		setValue(BACKUP_NEXT_NUMBER, false, String.valueOf(value));
	}

	public Integer getBackupMaxNumber() {
		try {
			return Integer.parseInt(getValue(BACKUP_MAX_HISTORY, false));
		} catch (Exception e) {
			return null;
		}
	}

	public void setBackupMaxNumber(int value) {
		setValue(BACKUP_MAX_HISTORY, false, String.valueOf(value));
	}

	public String getDatabaseUsed() {
		return getValue(DATABASE_USED, false);
	}

	public void setDatabaseUsed(String value) {
		setValue(DATABASE_USED, false, value);
	}

	public Long getDefaultAccountId(String preferenceName) {
		String value = getValue(preferenceName, false);
		if (value == null) {
			return null;
		}
		return Long.valueOf(value);
	}

	public void setDefaultAccountId(String preferenceName, Long value) {
		setValue(preferenceName, false, String.valueOf(value));
	}


	public Country getCountry() {
		String result = getValue(ACCOUNT_CURRENCY, false);
		if (result == null) {
			return Country.AUSTRALIA;
		}
		return Country.forCurrencySymbol(result);
	}

	public void setCountry(Country value) {
		setValue(ACCOUNT_CURRENCY, false, value.currencySymbol());
	}

	public Long getTaxPK() {
		String result = getValue(ACCOUNT_TAXPK, false);
		if (result == null) {
			return null;
		}
		return Long.valueOf(result);
	}

	public void setTaxPK(Long value) {
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

	public void setPayPeriodDays(int value) {
		setValue(PAY_PERIOD_DAYS, false, String.valueOf(value));
	}


	public synchronized Integer getAccountInvoiceTerms() {
		try {
			String value = getValue(ACCOUNT_INVOICE_TERMS, false);
			if (value == null) {
				setAccountInvoiceTerms(0);
				return 0;
			}
			return Integer.parseInt(value);
		} catch (Exception e) {
			return null;
		}
	}

	public void setAccountInvoiceTerms(int value) {
		if (PreferenceValidator.isValidAccountInvoiceTerms(value)) {
			setValue(ACCOUNT_INVOICE_TERMS, false, String.valueOf(value));
		}
	}

	public String getAccountPrepaidFeesPostAt() {
		String value = getValue(ACCOUNT_PREPAID_FEES_POST_AT, false);
		if (value == null) {
			setAccountPrepaidFeesPostAt(ACCOUNT_PREPAID_FEES_POST_AT_EVERY_SESSION);
			return ACCOUNT_PREPAID_FEES_POST_AT_EVERY_SESSION;
		}
		return value;
	}

	public void setAccountPrepaidFeesPostAt(String value) {
		setValue(ACCOUNT_PREPAID_FEES_POST_AT, false, value);
	}

	public boolean getShowRTOGUI() {
		String aPref = getValue(AVETMISS_SHOW_GUI, false);
		if (aPref == null) {
			return false;
		}
		return Boolean.parseBoolean(aPref);

	}

	public void setShowRTOGUI(boolean value) {
		setValue(AVETMISS_SHOW_GUI, false, Boolean.toString(value));
	}

	public String getAvetmissID() {
		return getValue(AVETMISS_ID, false);
	}

	public void setAvetmissID(String value) {
		setValue(AVETMISS_ID, false, value);
	}

	public ExportJurisdiction getAvetmissJurisdiction() {
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

	public String getAvetmissCollegeName() {
		return getValue(AVETMISS_COLLEGENAME, false);
	}

	public void setAvetmissCollegeName(String value) {
		setValue(AVETMISS_COLLEGENAME, false, value);
	}

	public String getAvetmissCollegeShortName() {
		return getValue(AVETMISS_COLLEGESHORTNAME, false);
	}

	public void setAvetmissCollegeShortName(String value) {
		setValue(AVETMISS_COLLEGESHORTNAME, false, value);
	}

	public String getAvetmissType() {
		return getValue(AVETMISS_TYPE, false);
	}

	public void setAvetmissType(String value) {
		setValue(AVETMISS_TYPE, false, value);
	}

	public synchronized String getAvetmissAddress1() {
		String result = getValue(AVETMISS_ADDRESS1, false);
		if (result == null) {
			setAvetmissAddress1(StringUtils.EMPTY);
			return StringUtils.EMPTY;
		}
		return result;
	}

	public void setAvetmissAddress1(String value) {
		setValue(AVETMISS_ADDRESS1, false, value);
	}

	public String getAvetmissAddress2() {
		return getValue(AVETMISS_ADDRESS2, false);
	}

	public void setAvetmissAddress2(String value) {
		setValue(AVETMISS_ADDRESS2, false, value);
	}

	public synchronized String getAvetmissSuburb() {
		String result = getValue(AVETMISS_SUBURB, false);
		if (result == null) {
			setAvetmissSuburb(StringUtils.EMPTY);
			return StringUtils.EMPTY;
		}
		return result;
	}

	public void setAvetmissSuburb(String value) {
		setValue(AVETMISS_SUBURB, false, value);
	}

	public String getAvetmissState() {
		return getValue(AVETMISS_STATE, false);
	}

	public String getAvetmissStateName() {
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

	public void setAvetmissState(String value) {
		setValue(AVETMISS_STATE, false, value);
	}

	public String getAvetmissPostcode() {
		String result = getValue(AVETMISS_POSTCODE, false);
		if (result == null) {
			return StringUtils.EMPTY;
		}
		return result;
	}

	public void setAvetmissPostcode(String value) {
		setValue(AVETMISS_POSTCODE, false, value);
	}

	public String getAvetmissContactName() {
		return getValue(AVETMISS_CONTACTNAME, false);
	}

	public void setAvetmissContactName(String value) {
		setValue(AVETMISS_CONTACTNAME, false, value);
	}

	public String getAvetmissPhone() {
		return getValue(AVETMISS_PHONE, false);
	}

	public void setAvetmissPhone(String value) {
		setValue(AVETMISS_PHONE, false, value);
	}

	public String getAvetmissFax() {
		return getValue(AVETMISS_FAX, false);
	}

	public void setAvetmissFax(String value) {
		setValue(AVETMISS_FAX, false, value);
	}

	public String getAvetmissEmail() {
		return getValue(AVETMISS_EMAIL, false);
	}

	public void setAvetmissEmail(String value) {
		setValue(AVETMISS_EMAIL, false, value);
	}

	public synchronized String getAvetmissCertSignatoryName() {
		String result = getValue(AVETMISS_CERT_SIGNATORY_NAME, false);
		if (result == null) {
			setAvetmissCertSignatoryName(StringUtils.EMPTY);
			return StringUtils.EMPTY;
		}
		return result;
	}

	public void setAvetmissCertSignatoryName(String value) {
		setValue(AVETMISS_CERT_SIGNATORY_NAME, false, value);
	}

	public String getAvetmissQldIdentifier() {
		return getValue(AVETMISS_QLD_IDENTIFIER, false);
	}

	public void setAvetmissQldIdentifier(String value) {
		setValue(AVETMISS_QLD_IDENTIFIER, false, value);
	}

	public String getAvetmissFeeHelpProviderCode() {
		return getValue(AVETMISS_FEE_HELP_PROVIDER_CODE, false);
	}

	public void setAvetmissFeeHelpProviderCode(String value) {
		setValue(AVETMISS_FEE_HELP_PROVIDER_CODE, false, value);
	}


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

	public void setCourseClassDefaultMinimumPlaces(Integer value) {
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

	public void setCourseClassDefaultMaximumPlaces(Integer value) {
		setValue(CLASS_DEFAULTS_MAXIMUM_PLACES, false, String.valueOf(value));
	}

	public DeliveryMode getCourseClassDefaultDeliveryMode() {
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

	public ClassFundingSource getCourseClassDefaultFundingSource() {
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

	public boolean getQEDefaultsToZeroPayment() {
		String aPref = getValue(QE_DEFAULTS_TO_ZERO, false);
		return aPref != null && Boolean.parseBoolean(aPref);

	}

	public void setQEDefaultsToZeroPayment(boolean value) {
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

	public void setDefaultQEEnrolmentReportKeycode(String value) {
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

	public void setDefaultQEInvoiceReportKeycode(String value) {
		setValue(QE_DEFAULT_REPORT_INVOICE_KEYCODE, false, value);
	}

	public boolean getGravatarEnabled() {
		String value = getValue(GRAVATAR, false);
		if (StringUtils.isEmpty(value)) {
			return Boolean.TRUE;
		}
		return Boolean.parseBoolean(value);
	}

	public String getToolbarActiveTab() {
		return getValue(TOOLBAR_ACTIVE_TAB, true);
	}

	public void setToolbarActiveTab(String toolbarActiveTab) {
		setValue(TOOLBAR_ACTIVE_TAB, true, toolbarActiveTab);
	}

	public String getEulaAgreement(String userLogin) {
		return getValue(EULA_AGREEMENT + userLogin, true);
	}

	public void setEulaAgreement(String userLogin, String revision) {
		setValue(EULA_AGREEMENT + userLogin, true, revision);
	}

	public Rectangle getFramePosition(String frameIdentifier) {
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

	public void setFramePosition(String frameIdentifier, Rectangle value) {
		String prefValue = (int) value.getX() + "," + (int) value.getY() + "," + (int) value.getWidth() + "," + (int) value.getHeight();
		setValue(FRAME_BOUNDS + frameIdentifier, true, prefValue);
	}

	public boolean getToolbarCollapsed() {
		String value = getValue(TOOLBAR_COLLAPSE_STATE, true);
		if (value == null) {
			return false;
		}
		return Boolean.parseBoolean(value);
	}

	public void setToolbarCollapsed(boolean value) {
		setValue(TOOLBAR_COLLAPSE_STATE, true, Boolean.toString(value));
	}

	/**
	 * Get the list view column order and sizing.
	 *
	 * @param frameIdentifier a unique string identifier for this view
	 * @return this is a map of column property keys and widths (in pixels)
	 */
	public Map<String, Integer> getListViewColumns(String frameIdentifier) {
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
	public void setListViewColumns(String frameIdentifier, Map<String, Integer> value) {
		StringBuilder prefValue = new StringBuilder();
		for (Map.Entry<String, Integer> col : value.entrySet()) {
			prefValue.append(col.getKey()).append(":").append(col.getValue()).append(",");
		}
		prefValue.deleteCharAt(prefValue.length() - 1);
		setValue(LISTVIEW_COLUMNS + frameIdentifier, true, prefValue.toString());
	}

	public void setListViewDividerPosition(String frameIdentifier, int value) {
		setValue(LISTVIEW_DIVIDER + frameIdentifier, true, String.valueOf(value));
	}

	public Integer getListViewDividerPosition(String frameIdentifier) {
		try {
			return Integer.parseInt(getValue(LISTVIEW_DIVIDER + frameIdentifier, true));
		} catch (Exception e) {
			return null;
		}
	}

	public void setQEViewDividerPosition(String frameIdentifier, int value) {
		setValue(QEVIEW_DIVIDER + frameIdentifier, true, String.valueOf(value));
	}

	public Integer getQEViewDividerPosition(String frameIdentifier) {
		try {
			return Integer.parseInt(getValue(QEVIEW_DIVIDER + frameIdentifier, true));
		} catch (Exception e) {
			return null;
		}
	}

	public void setFilterCollapsingState(String filterIdentifier, boolean value) {
		setValue(LISTVIEW_FILTERS_COLLAPSING + filterIdentifier, true, String.valueOf(value));
	}


	public boolean getFilterCollapsingState(String filterIdentifier) {
		return Boolean.parseBoolean(getValue(LISTVIEW_FILTERS_COLLAPSING + filterIdentifier, true));
	}

	public void setListViewSortedColumns(String frameIdentifier, Map<String, Boolean> sortedColumns) {
		StringBuilder prefValue = new StringBuilder();
		for (Map.Entry<String, Boolean> col : sortedColumns.entrySet()) {
			prefValue.append(String.format("%s:%b,", col.getKey(), col.getValue()));
		}
		setValue(LISTVIEW_COLUMN_SORTED + frameIdentifier, true, prefValue.toString());
	}

	public Map<String, Boolean> getListViewSortedColumns(String frameIdentifier) {
		String value = getValue(LISTVIEW_COLUMN_SORTED + frameIdentifier, true);
		if (StringUtils.trimToNull(value) != null) {
			String[] elements = commaExplode.split(value);
			Map<String, Boolean> sortedColumn  = new LinkedHashMap<>(elements.length);
			for (String col : elements) {
				String[] colIndexSorting = colonExplode.split(col);
				if (colIndexSorting.length == 2) {
					sortedColumn.put(colIndexSorting[0], Boolean.parseBoolean(colIndexSorting[1]));
				}
			}
			return sortedColumn;
		}
		return null;
	}

	/**
	 * Get the server host name used for the last login of the client on this workstation.
	 *
	 * @return host name
	 */
	public static String getLastLoginServerHost() {
		return getFilePreference(LASTLOGIN_SERVER_HOST, "localhost");
	}

	/**
	 * Set the server host name to be used for the next login of the client on this workstation.
	 *
	 * @param value host name
	 */
	public static void setLastLoginServerHost(String value) {
		setFilePreference(LASTLOGIN_SERVER_HOST, value);
	}

	/**
	 * Get the server port used for the last login of the client on this workstation.
	 *
	 * @return TCP port
	 */
	public static int getLastLoginServerPort() {
		String value = getFilePreference(LASTLOGIN_SERVER_PORT, "443");
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
	public static void setLastLoginServerPort(int value) {
		setFilePreference(LASTLOGIN_SERVER_PORT, Integer.toString(value));
	}

	/**
	 * Returns weather the last connection to the server was a secure one (SSL)
	 */
	public static Boolean getLastLoginServerIsSsl() {
		String value = getFilePreference(LASTLOGIN_SERVER_ISSSL, "false");
		if (value == null) {
			return false;
		}
		return Boolean.parseBoolean(value);
	}

	public static void setLastLoginServerIsSsl(Boolean value) {
		setFilePreference(LASTLOGIN_SERVER_ISSSL, String.valueOf(value));
	}

	/**
	 * Get the user name used for the last login of the client on this workstation.
	 *
	 * @return user name
	 */
	public static String getLastLoginServerUserName() {
		return getFilePreference(LASTLOGIN_USERNAME, "");
	}

	/**
	 * Set the user name to be used for the next login of the client on this workstation.
	 *
	 * @param value user name
	 */
	public static void setLastLoginServerUserName(String value) {
		setFilePreference(LASTLOGIN_USERNAME, value);
	}

	public static String getSHA1Fingerprint(String host) {
		return getFilePreference(host, "");
	}

	public static void setSHA1Fingerprint(String host, String value) {
		setFilePreference(host, value);
	}

	public File getExportMailingListDestination() {
		String dir = getFilePreference(MAILINGLIST_EXPORT_FOLDER, null);
		if (dir == null || dir.length() == 0) {
			dir = System.getProperty("user.dir");
		}
		return new File(dir);
	}

	public void setExportMailingListDestination(File value) {
		setFilePreference(MAILINGLIST_EXPORT_FOLDER, value.getAbsolutePath());
	}

	public File getExportPdfDestination() {
		String dir = getFilePreference(REPORT_PDF_FOLDER, null);
		if (dir == null || dir.length() == 0) {
			dir = System.getProperty("user.dir");
		}
		return new File(dir);
	}

	public void setExportPdfDestination(File value) {
		setFilePreference(REPORT_PDF_FOLDER, value.getAbsolutePath());
	}

	public File getExportExcelDestination() {
		String dir = getFilePreference(REPORT_XLS_FOLDER, null);
		if (dir == null || dir.length() == 0) {
			dir = System.getProperty("user.dir");
		}
		return new File(dir);
	}

	public void setExportExcelDestination(File value) {
		setFilePreference(REPORT_XLS_FOLDER, value.getAbsolutePath());
	}

	public File getReportImportSource() {
		String dir = getFilePreference(REPORT_IMPORT_FOLDER, null);
		if (dir == null || dir.length() == 0) {
			dir = System.getProperty("user.dir");
		}
		return new File(dir);
	}

	public void setReportImportSource(File value) {
		setFilePreference(REPORT_IMPORT_FOLDER, value.getAbsolutePath());
	}

	public File getExportTemplateImportSource() {
		String dir = getFilePreference(EXPORTTEMPLATE_IMPORT_FOLDER, null);
		if (dir == null || dir.length() == 0) {
			dir = System.getProperty("user.dir");
		}
		return new File(dir);
	}

	public void setExportTemplateImportSource(File value) {
		setFilePreference(EXPORTTEMPLATE_IMPORT_FOLDER, value.getAbsolutePath());
	}

	public File getAvetmissExportPath() {
		String dir = getFilePreference(AVETMISS_EXPORT_PATH, null);
		if (dir == null || dir.length() == 0) {
			dir = System.getProperty("user.dir");
		}
		return new File(dir);
	}

	public void setAvetmissExportPath(File value) {
		setFilePreference(AVETMISS_EXPORT_PATH, value.getAbsolutePath());
	}

	public File getDocumentImportPath() {
		String dir = getFilePreference(DOCUMENT_IMPORT_PATH, null);
		if (dir == null || dir.length() == 0) {
			dir = System.getProperty("user.dir");
		}
		return new File(dir);
	}

	public void setDocumentImportPath(File value) {
		setFilePreference(DOCUMENT_IMPORT_PATH, value.getAbsolutePath());
	}

	public void setStorageBucketName(String value) {
		setValue(STORAGE_BUCKET_NAME, false, value);
	}

	public String getStorageBucketName() {
		return getValue(STORAGE_BUCKET_NAME, false);
	}

	public void setStorageAccessId(String value) {
		setValue(STORAGE_ACCESS_ID, false, value);
	}

	public String getStorageAccessId() {
		return getValue(STORAGE_ACCESS_ID, false);
	}

	public void setStorageAccessKey(String value) {
		setValue(STORAGE_ACCESS_KEY, false, value);
	}

	public String getStorageAccessKey() {
		return getValue(STORAGE_ACCESS_KEY, false);
	}

	public boolean isUsingExternalStorage() {
		return StringUtils.trimToNull(getStorageAccessId()) != null;
	}

	public void setAuskeyPassword(String value) {
		setValue(AUSKEY_PASSWORD, false, value);
	}

	public String getAuskeyPassword() {
		return getValue(AUSKEY_PASSWORD, false);
	}

	public void setAuskeyCertificate(String value) {
		setValue(AUSKEY_CERTIFICATE, false, value);
	}

	public String getAuskeyCertificate() {
		return getValue(AUSKEY_CERTIFICATE, false);
	}

	public void setAuskeyPrivateKey(String value) {
		setValue(AUSKEY_PRIVATE_KEY, false, value);
	}

	public String getAuskeyPrivateKey() {
		return getValue(AUSKEY_PRIVATE_KEY, false);
	}

	public void setAuskeySalt(String value) {
		setValue(AUSKEY_SALT, false, value);
	}

	public String getAuskeySalt() {
		return getValue(AUSKEY_SALT, false);
	}

	public boolean getUseOnlyOfferedModulesAndQualifications() {
		return Boolean.parseBoolean(getValue(USE_ONLY_OFFERED_MODULES_AND_QUALIFICATIONS, false));
	}

	public void setUseOnlyOfferedModulesAndQualifications(boolean value) {
		setValue(USE_ONLY_OFFERED_MODULES_AND_QUALIFICATIONS, false, "" + value);
	}

	public Date getMYOBLastExportDate() {
		if (getValue(MYOB_LAST_EXPORT_DATE, false) == null) {
			return null;
		}
		try {
			return dateFormat.parse(getValue(MYOB_LAST_EXPORT_DATE, false));
		} catch (ParseException e) {
			return null;
		}
	}

	public void setMYOBLastExportDate(Date value) {
		setValue(MYOB_LAST_EXPORT_DATE, false, dateFormat.format(value));
	}
	/*
	 * Utility methods
	 */
	public boolean hasEmailBeenConfigured() {
		String fromAddress = getEmailFromAddress();
		return fromAddress != null && fromAddress.length() != 0;
	}

	public boolean hasSMSBeenConfigured() {
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
	public Object getValueForKey(String key) {

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
		} else if (AVETMISS_QLD_IDENTIFIER.equals(key)) {
			return getAvetmissQldIdentifier();
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
		} else if (DOCUMENT_IMPORT_PATH.equals(key)) {
			return getDocumentImportPath();
		} else if (ONCOURSE_SERVER_DEFAULT_TZ.equals(key)) {
			return getOncourseServerDefaultTimezone();
		} else if (ACCOUNT_INVOICE_TERMS.equals(key)) {
			return getAccountInvoiceTerms();
		} else if (AVETMISS_FEE_HELP_PROVIDER_CODE.equals(key)) {
			return getAvetmissFeeHelpProviderCode();
		} else if (AUTO_DISABLE_INACTIVE_ACCOUNT.equals(key)) {
			return getAutoDisableInactiveAccounts();
		} else if (PASSWORD_COMPLEXITY.equals(key)) {
			return getPasswordComplexity();
		} else if (PASSWORD_EXPIRY_PERIOD.equals(key)) {
			return getPasswordExpiryPeriod();
		} else if (TWO_FACTOR_AUTHENTICATION.equals(key)) {
			return getTwoFactorAuthStatus();
		} else if (TFA_EXPIRY_PERIOD.equals(key)) {
			return getTwoFactorAuthExpiryPeriod();
		}

		if (DEPRECATED_PREFERENCES.contains(key)) {
			logger.info("accessing deprecated preference : " + key);
			return null;
		}

		throw new IllegalArgumentException("Key not found. (" + key + ")");
	}

	public void setValueForKey(String key, Object value) {

		if (EMAIL_ADMIN_ADDRESS.equals(key) || EMAIL_FROM_ADDRESS.equals(key) || EMAIL_BOUNCEADDRESS.equals(key)) {
			String localValue = (String) value;
			if (localValue != null && WRAPPING_QUOTATION_MARKS.matcher(localValue).matches()) {
				value = localValue.replace('"', ' ');
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
		} else if (AVETMISS_QLD_IDENTIFIER.equals(key)) {
			setAvetmissQldIdentifier((String) value);
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
		} else if (DOCUMENT_IMPORT_PATH.equals(key)) {
			setDocumentImportPath((File) value);
		} else  if (ONCOURSE_SERVER_DEFAULT_TZ.equals(key)) {
			setOncourseServerDefaultTimezone((String) value);
		} else if (COLLEGE_PAYMENT_INFO.equals(key)) {
			setPaymentInfo((String) value);
		} else if (ACCOUNT_INVOICE_TERMS.equals(key)) {
			setAccountInvoiceTerms((Integer) value);
		} else if (AVETMISS_FEE_HELP_PROVIDER_CODE.equals(key)) {
			setAvetmissFeeHelpProviderCode((String) value);
		} else if (AUTO_DISABLE_INACTIVE_ACCOUNT.equals(key)) {
			setAutoDisableInactiveAccounts((Boolean) value);
		} else if (PASSWORD_COMPLEXITY.equals(key)) {
			setPasswordComplexity((Boolean) value);
		} else if (PASSWORD_EXPIRY_PERIOD.equals(key)) {
			setPasswordExpiryPeriod((Integer) value);
		} else if (TWO_FACTOR_AUTHENTICATION.equals(key)) {
			setTwoFactorAuthStatus((TwoFactorAuthorizationStatus) value);
		} else if (TFA_EXPIRY_PERIOD.equals(key)) {
			setTwoFactorAuthExpiryPeriod((Integer) value);
		}
	}

	/**
	 * Communication key using to track college sessions during replication to prevent copy of data being started to replicate.
	 *
	 * @return communication key
	 */
	public Long getCommunicationKey() {
		synchronized (this) {
			String value = getValue(SERVICES_COMMUNICATION_KEY, false);

			if (value == null) {
				Random randomGen = new Random();
				long newCommunicationKey = ((long) randomGen.nextInt(63) << 59) + System.currentTimeMillis();
				setCommunicationKey(newCommunicationKey);
				return newCommunicationKey;
			}

			return Long.valueOf(value);
		}
	}

	/**
	 * Sets communicaiton key to being used during the next replication session.
	 *
	 * @param communicationKey
	 */
	public void setCommunicationKey(Long communicationKey) {
		setValue(SERVICES_COMMUNICATION_KEY, false, String.valueOf(communicationKey));
	}

	/**
	 * Shows if replication should be performed for college.
	 */
	public boolean getReplicationEnabled() {
		return Boolean.parseBoolean(getValue(REPLICATION_ENABLED, false));
	}

	/**
	 * Enabled or disable replication. Should be used only in unit tests.
	 *
	 * @param value
	 */
	public void setReplicationEnabled(boolean value) {
		setValue(REPLICATION_ENABLED, false, Boolean.toString(value));
	}

	public boolean getFeatureConcessionsInEnrolment() {
		return Boolean.parseBoolean(getValue(FEATURE_CONCESSIONS_IN_ENROLMENT, false));
	}

	public void setFeatureConcessionsInEnrolment(boolean value) {
		setValue(FEATURE_CONCESSIONS_IN_ENROLMENT, false, Boolean.toString(value));
	}

	public void setFeatureConcessionsUsersCreate(boolean value) {
		setValue(FEATURE_CONCESSION_USERS_CREATE, false, Boolean.toString(value));
	}

	public boolean getFeatureConcessionsUsersCreate() {
		return Boolean.parseBoolean(getValue(FEATURE_CONCESSION_USERS_CREATE, false));
	}

	public String getFeatureEnrolmentDisclosure() {
		return getValue(FEATURE_ENROLMENT_DISCLOSURE, false);
	}

	public void setFeatureEnrolmentDisclosure(String value) {
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
	public static Object deserializeObject(byte[] data) {
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
	public static byte[] serializeObject(Object object) {
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
	protected static String getFilePreference(String key, String valueIfNull) {
		return FILE_PREFS.get(key, valueIfNull);
	}

	/**
	 * Sets a user's system preference value for the given key
	 *
	 * @param key
	 * @param value
	 */
	protected static void setFilePreference(String key, String value) {
		FILE_PREFS.put(key, value);
	}

	public static CommonPreferenceController getController() {
		return sharedController;
	}

	public String getPortalHideClassRollContactPhone() {
		return getValue(PORTAL_HIDE_CLASS_ROLL_CONTACT_PHONE, false);
	}

	public void setPortalHideClassRollContactPhone(String value) {
		setValue(PORTAL_HIDE_CLASS_ROLL_CONTACT_PHONE, false, value);
	}

	public String getPortalHideClassRollContactEmail() {
		return getValue(PORTAL_HIDE_CLASS_ROLL_CONTACT_EMAIL, false);
	}

	public void setPortalHideClassRollContactEmail(String value) {
		setValue(PORTAL_HIDE_CLASS_ROLL_CONTACT_EMAIL, false, value);
	}

	private static final boolean DEF_INACTIVE_ACCOUNT = true;
	private static final boolean DEF_PASSWORD_COMPLEXITY = false;
	private static final TwoFactorAuthorizationStatus DEF_TFA_STATUS = TwoFactorAuthorizationStatus.DISABLED;

	public boolean getAutoDisableInactiveAccounts() {
		String value = getValue(AUTO_DISABLE_INACTIVE_ACCOUNT, false);
		return value == null ? DEF_INACTIVE_ACCOUNT : Boolean.parseBoolean(value);
	}

	public void setAutoDisableInactiveAccounts(boolean value) {
		setValue(AUTO_DISABLE_INACTIVE_ACCOUNT, false,Boolean.toString(value));
	}

	public boolean getPasswordComplexity() {
		String value = getValue(PASSWORD_COMPLEXITY, false);
		return value == null ? DEF_PASSWORD_COMPLEXITY : Boolean.parseBoolean(value);
	}

	public void setPasswordComplexity(boolean value) {
		setValue(PASSWORD_COMPLEXITY, false, Boolean.toString(value));
	}


	public Integer getPasswordExpiryPeriod() {
		String value = getValue(PASSWORD_EXPIRY_PERIOD, false);
		return value == null ? null : Integer.parseInt(value);
	}

	public void setPasswordExpiryPeriod(Integer value) {
		setValue(PASSWORD_EXPIRY_PERIOD, false, value == null ? null : Integer.toString(value));
	}

	public TwoFactorAuthorizationStatus getTwoFactorAuthStatus() {
		String value = getValue(TWO_FACTOR_AUTHENTICATION, false);
		return value == null ? DEF_TFA_STATUS : TypesUtil.getEnumForDatabaseValue(value, TwoFactorAuthorizationStatus.class);
	}

	public void setTwoFactorAuthStatus(TwoFactorAuthorizationStatus value) {
		setValue(TWO_FACTOR_AUTHENTICATION, false, value == null ? DEF_TFA_STATUS.getDatabaseValue() : value.getDatabaseValue());
	}

	public Integer getTwoFactorAuthExpiryPeriod() {
		String value = getValue(TFA_EXPIRY_PERIOD, false);
		return value == null ? null : Integer.parseInt(value);
	}

	public void setTwoFactorAuthExpiryPeriod(Integer value) {
		setValue(TFA_EXPIRY_PERIOD, false, value == null ? null : Integer.toString(value));
	}
}
