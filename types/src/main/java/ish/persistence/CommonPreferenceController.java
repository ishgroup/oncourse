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
import ish.validation.PreferenceValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import static ish.persistence.Preferences.*;

public abstract class CommonPreferenceController {

	private static final Logger logger = LogManager.getLogger();

	private static final java.util.prefs.Preferences FILE_PREFS = java.util.prefs.Preferences.userNodeForPackage(CommonPreferenceController.class);
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


	public  String getPaymentGatewayPass() {
		String apiKey = getValue(PAYMENT_GATEWAY_PASS, false);
		if (apiKey == null) {
			throw new IllegalArgumentException();
		}
		return apiKey;
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

	public boolean getLicenseAccessControl() {
		return Boolean.parseBoolean(getValue(LICENSE_ACCESS_CONTROL, false));
	}

	public void setLicenseAccessControl(boolean value) {
		throw new IllegalStateException("Licences must replicate from ish");
// setValue(LICENSE_ACCESS_CONTROL, false, Boolean.toString(value));
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
		return Boolean.parseBoolean(getValue(LICENSE_PAYROLL, false));
	}

	public void setLicensePayroll(boolean value) {
		throw new IllegalStateException("Licences must replicate from ish");
// setValue(LICENSE_WEBSITE, false, Boolean.toString(value));
	}

	public boolean getLicenseVoucher() {
		return Boolean.parseBoolean(getValue(LICENSE_VOUCHER, false));
	}

	public void setLicenseVoucher(boolean value) {
		throw new IllegalStateException("Licences must replicate from ish");
	}

	public boolean getLicenseMembership() {
		return Boolean.parseBoolean(getValue(LICENSE_MEMBERSHIP, false));
	}

	public void setLicenseMembership(boolean value) {
		throw new IllegalStateException("Licences must replicate from ish");
	}

	public boolean getLicenseAttendance() {
		return Boolean.parseBoolean(getValue(LICENSE_ATTENDANCE, false));
	}

	public void setLicenseAttendance(boolean value) {
		throw new IllegalStateException("Licences must replicate from ish");
	}

	public boolean getLicenseScripting() {
		return Boolean.parseBoolean(getValue(LICENSE_SCRIPTING, false));
	}

	public boolean getLicenseFeeHelpExport () {
		return Boolean.parseBoolean(getValue(LICENSE_FEE_HELP_EXPORT, false));
	}

	public void setLicenseScripting(boolean value) {
		throw new IllegalStateException("Licences must replicate from ish");
	}

	public boolean getLicenseFundingContract() {
		return Boolean.parseBoolean(getValue(LICENSE_FUNDING_CONTRACT, false));
	}

	public void setLicenseFundingContract(boolean value) {
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

	public boolean getGravatarEnabled() {
		String value = getValue(GRAVATAR, false);
		if (StringUtils.isEmpty(value)) {
			return Boolean.TRUE;
		}
		return Boolean.parseBoolean(value);
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
		} else if (AVETMISS_JURISDICTION.equals(key)) {
			return getAvetmissJurisdiction();
		} else if (COLLEGE_PAYMENT_INFO.equals(key)) {
			return getPaymentInfo();
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
		} else if (GRAVATAR.equals(key)) {
			return getGravatarEnabled();
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
		} else if (NUMBER_OF_LOGIN_ATTEMPTS.equals(key)) {
			return getNumberOfLoginAttempts();
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
		} else if (AVETMISS_JURISDICTION.equals(key)) {
			setAvetmissJurisdiction((ExportJurisdiction) value);
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
		} else if (NUMBER_OF_LOGIN_ATTEMPTS.equals(key)) {
			setNumberOfLoginAttempts((Integer) value);
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
	@Deprecated
	public boolean getReplicationEnabled() {
		return Boolean.parseBoolean(getValue(REPLICATION_ENABLED, false));
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
	private static final Integer DEF_NUMBER_LOGIN_ATTEMPTS = 5;
	private static final TwoFactorAuthorizationStatus DEF_TFA_STATUS = TwoFactorAuthorizationStatus.DISABLED;

	public boolean getAutoDisableInactiveAccounts() {
		String value = getValue(AUTO_DISABLE_INACTIVE_ACCOUNT, false);
		return value == null ? DEF_INACTIVE_ACCOUNT : Boolean.parseBoolean(value);
	}

	public void setAutoDisableInactiveAccounts(boolean value) {
		setValue(AUTO_DISABLE_INACTIVE_ACCOUNT, false, Boolean.toString(value));
	}

	public Integer getNumberOfLoginAttempts() {
		String value = getValue(NUMBER_OF_LOGIN_ATTEMPTS, false);
		return value == null ? DEF_NUMBER_LOGIN_ATTEMPTS : Integer.valueOf(value);
	}

	public void setNumberOfLoginAttempts(Integer value) {
		setValue(NUMBER_OF_LOGIN_ATTEMPTS, false, value == null ? null : Integer.toString(value));
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
