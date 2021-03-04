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
package ish.oncourse.server;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import ish.common.types.DeliverySchedule;
import ish.math.Country;
import ish.math.CurrencyFormat;
import ish.oncourse.server.cayenne.Preference;
import ish.oncourse.server.cayenne.SurveyFieldConfiguration;
import ish.oncourse.server.cayenne.SystemUser;
import ish.oncourse.server.license.LicenseService;
import ish.oncourse.server.services.ISystemUserService;
import ish.persistence.CommonPreferenceController;
import ish.persistence.Preferences;
import ish.util.SecurityUtil;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static ish.oncourse.DefaultAccount.defaultAccountPreferences;
import static ish.persistence.Preferences.*;

@Singleton
public class PreferenceController extends CommonPreferenceController {

	public static final Integer DEFAULT_TIMEOUT_SEC = 3600;
	public static final Long DEFAULT_TIMEOUT_MS = DEFAULT_TIMEOUT_SEC * 1000L;

	private static final Logger logger = LogManager.getLogger();

	private final ObjectContext context;
	private final ISystemUserService systemUserService;
	private final LicenseService licenseService;

	private Map<String, Boolean> showReleaseNotes = new HashMap<>();

	@Inject
	public PreferenceController(ICayenneService cayenneService, ISystemUserService systemUserService,LicenseService licenseService) {
		this.context = cayenneService.getNewContext();
		this.systemUserService = systemUserService;
		this.licenseService = licenseService;
		sharedController = this;
	}


	public Long getTimeoutMs() {
		Integer timeout =  getTimeoutSec();
		return 	timeout > 0 ? timeout * 1000L : DEFAULT_TIMEOUT_MS;
	}

	public Integer getTimeoutSec() {
		Long timeoutMinutes =  getLogoutTimeout();
		return 	timeoutMinutes > 0 ? timeoutMinutes.intValue() * 60 : DEFAULT_TIMEOUT_SEC;
	}
	public Date getTimeoutThreshold() {
		long timeoutThresholdMs = new Date().getTime() - getTimeoutMs();
		return  new Date(timeoutThresholdMs);
	}

	/**
	 * Instead of the timeout relative to now, get it relative to the time
	 * passed in milliseconds
	 * @param time
	 * @return
	 */
	public Date getTimeoutThreshold(long time) {
		long timeoutThresholdMs = time - getTimeoutMs();
		return  new Date(timeoutThresholdMs);
	}

	@Override
	public Object getValueForKey(String key) {
		if (defaultAccountPreferences.contains(key)) {
			return getDefaultAccountId(key);
		}

		switch (key) {
			case LicenseService.SERVICES_SECURITYKEY:
				String securityKey = licenseService.getSecurity_key();
				if (securityKey != null) {
					return org.apache.commons.lang3.StringUtils.right(securityKey, 4);
				} else {
					return null;
				}
			case LICENSE_ACCESS_CONTROL:
				return getLicenseAccessControl();
			case LICENSE_LDAP:
				return getLicenseLdap();
			case LICENSE_BUDGET :
				return getLicenseBudget();
			case LICENSE_EXTENRNAL_DB:
				return getLicenseExternalDB();
			case LICENSE_SSL:
				return null;
			case LICENSE_SMS:
				return getLicenseSms();
			case LICENSE_CC_PROCESSING:
				return getLicenseCCProcessing();
			case LICENSE_PAYROLL:
				return getLicensePayroll();
			case LICENSE_VOUCHER :
				return getLicenseVoucher();
			case LICENSE_MEMBERSHIP:
				return getLicenseMembership();
			case LICENSE_ATTENDANCE:
				return getLicenseAttendance();
			case LICENSE_SCRIPTING:
				return getLicenseScripting();
			case LICENSE_FEE_HELP_EXPORT :
				return getLicenseFeeHelpExport();
			case LICENSE_FUNDING_CONTRACT:
				return getLicenseFundingContract();
			case DATABASE_USED:
				return getDatabaseUsed();
			case USI_SOFTWARE_ID:
				return getUsiSoftwareId();
			default:
				return super.getValueForKey(key);
		}
	}

	public synchronized String getUsiSoftwareId() {
		String id = getValue(USI_SOFTWARE_ID, false);
		if (id == null) {
			id = SecurityUtil.generateUSISoftwareId();
			setValue(USI_SOFTWARE_ID, false, id);
		}
		return id;
	}

	public void setValueForKey(String key, Object value) {
	    if ((key.equals(ACCOUNT_CURRENCY)) && (value != null)) {
			var country = (Country) value;
	        CurrencyFormat.updateLocale(country.locale());
        }
        if (defaultAccountPreferences.contains(key)) {
            setDefaultAccountId(key, (Long)value);
        } else {
            super.setValueForKey(key,value);
        }
	}



	/**
	 * @deprecated Replace with Google Guice injection.
	 */
	@Deprecated
	public synchronized static CommonPreferenceController getController() {
		return sharedController;
	}

	public void rollbackChanges() {
		this.context.rollbackChanges();
	}

	/**
	 * Retrieve the preference from the database.
	 *
	 * @param isUserPref true if specific preference for the currently logged in user
	 * @param key property key
	 * @return String value of preference
	 */
	public synchronized Preference getPreference(String key, boolean isUserPref) {
		var objectSelect = ObjectSelect.query(Preference.class).where(Preference.NAME.eq(key));

		if (isUserPref) {
			objectSelect.and(Preference.USER.eq(systemUserService.getCurrentUser()));
		}

		return objectSelect.selectFirst(context);
	}

	/**
	 * @see CommonPreferenceController#getValue(String, boolean)
	 */
	@Override
	protected synchronized String getValue(String key, boolean isUserPref) {
		var pref = getPreference(key, isUserPref);
		if (pref == null) {
			return null;
		}
		return pref.getValueString();
	}

	/**
	 * @see CommonPreferenceController#setValue(String, boolean, String)
	 */
	@Override
	protected synchronized void setValue(String key, boolean isUserPref, String value) {
		setValue(this.context, key, isUserPref, value);
	}

	private void setValue(ObjectContext context, String key, boolean isUserPref, String value) {
		var pref = getPreference(key, isUserPref);

		if (pref == null) {
			pref = context.newObject(Preference.class);
			pref.setName(key);
		} else {
			pref = context.localObject(pref);
		}

		pref.setValueString(StringUtils.trim(value));
		logger.debug("committing changes to prefs with value: {}", value);

		context.commitChanges();
	}

	public boolean isFirstTimeLoginAfterUpdate(String username) {
		if (showReleaseNotes.containsKey(username)) {
			showReleaseNotes.remove(username);
			return true;
		}
		return false;
	}

	public void updateReleaseNotesMap(SystemUser systemUser) {
		var preference = getPreference(Preferences.DATA_WED_VERSION, false);

		if (systemUser.getLastLoginOn() == null || systemUser.getLastLoginOn().compareTo(preference.getModifiedOn()) < 0) {
			showReleaseNotes.put(systemUser.getEmail(), true);
		}
	}

	public boolean hasSurveyForm() {
		return !ObjectSelect.query(SurveyFieldConfiguration.class)
				.where(SurveyFieldConfiguration.INT_TYPE.eq(4))
				.and(SurveyFieldConfiguration.DELIVERY_SCHEDULE.eq(DeliverySchedule.ON_ENROL))
				.select(context).isEmpty();
	}

}
