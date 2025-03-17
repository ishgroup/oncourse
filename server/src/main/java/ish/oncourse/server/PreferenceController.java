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
import ish.oncourse.server.cayenne.Preference;
import ish.oncourse.server.display.DisplayService;
import ish.oncourse.server.integration.PluginsPrefsService;
import ish.oncourse.server.license.LicenseService;
import ish.oncourse.server.services.ISchedulerService;
import ish.oncourse.server.services.ISystemUserService;
import ish.oncourse.server.services.UserDisableJob;
import ish.persistence.CommonPreferenceController;
import ish.util.SecurityUtil;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobKey;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static ish.oncourse.DefaultAccount.defaultAccountPreferences;
import static ish.oncourse.server.services.ISchedulerService.BACKGROUND_JOBS_GROUP_ID;
import static ish.oncourse.server.services.ISchedulerService.USER_DISAIBLE_JOB_ID;
import static ish.persistence.Preferences.*;

@Singleton
public class PreferenceController extends CommonPreferenceController {

	public static final Integer DEFAULT_TIMEOUT_SEC = 3600;
	public static final Long DEFAULT_TIMEOUT_MS = DEFAULT_TIMEOUT_SEC * 1000L;

	private static final Logger logger = LogManager.getLogger();

	private final ICayenneService cayenneService;
	private final ISystemUserService systemUserService;
	private final LicenseService licenseService;
	private final PluginsPrefsService pluginsPrefsService;
	private final ISchedulerService schedulerService;

	private ObjectContext objectContext;

	public static final List<String> CUSTOM_LOGO_PREFERENCES = List.of(
			CUSTOM_LOGO_BLACK, CUSTOM_LOGO_BLACK_SMALL,
			CUSTOM_LOGO_WHITE, CUSTOM_LOGO_WHITE_SMALL,
			CUSTOM_LOGO_COLOUR, CUSTOM_LOGO_COLOUR_SMALL
	);

    @Inject
    private DisplayService displayService;

	@Inject
	public PreferenceController(ICayenneService cayenneService, ISystemUserService systemUserService,
								LicenseService licenseService, PluginsPrefsService pluginsPrefsService,
								ISchedulerService schedulerService) {
		this.cayenneService = cayenneService;
		this.systemUserService = systemUserService;
		this.licenseService = licenseService;
		this.pluginsPrefsService = pluginsPrefsService;
		this.schedulerService = schedulerService;
		sharedController = this;
	}


	public Boolean getAusReporting() {
		return displayService.getAusReporting();
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

	public Object getValueForKey(String key) {
		if (defaultAccountPreferences.contains(key)) {
			return getDefaultAccountId(key);
		}

		if(key.equals(AUS_REPORTING))
			return getAusReporting();

		if(key.startsWith("plugins.")){
			return pluginsPrefsService.getProperty(key);
		}

		if (key.startsWith("license.")) {
			return licenseService.getLisense(key);
		}

		switch (key) {
			case LicenseService.SERVICES_SECURITYKEY:
				String securityKey = licenseService.getSecurity_key();
				if (securityKey != null) {
					return org.apache.commons.lang3.StringUtils.right(securityKey, 4);
				} else {
					return null;
				}
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
        if (defaultAccountPreferences.contains(key)) {
            setDefaultAccountId(key, (Long)value);
        } else {
            if (key.equals(AUTO_DISABLE_INACTIVE_ACCOUNT)) {
                try {
                    if ((Boolean) value) {
                        var random = new Random();
                        var randomSchedule = String.format(schedulerService.USER_DISAIBLE_JOB_TEMPLATE, random.nextInt(59));
                        schedulerService.scheduleCronJob(UserDisableJob.class,
                                USER_DISAIBLE_JOB_ID, BACKGROUND_JOBS_GROUP_ID,
                                randomSchedule, getOncourseServerDefaultTimezone(),
                                true, false);
                    } else {
                        schedulerService.removeJob(JobKey.jobKey(ISchedulerService.USER_DISAIBLE_JOB_ID, ISchedulerService.BACKGROUND_JOBS_GROUP_ID));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

			if (CUSTOM_LOGO_PREFERENCES.contains(key)) {
				setValue(key, false, Optional.ofNullable(value).orElse("").toString());
			}

			super.setValueForKey(key, value);
        }
	}

	/**
	 * @deprecated Replace with Google Guice injection.
	 */
	@Deprecated
	public synchronized static CommonPreferenceController getController() {
		return sharedController;
	}

	public ObjectContext getObjectContext() {
		if (objectContext == null) {
			objectContext = cayenneService.getNewContext();
		}
		return objectContext;
	}

	public void rollbackChanges() {
		this.getObjectContext().rollbackChanges();
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

		return objectSelect.selectFirst(cayenneService.getNewContext());
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
		setValue(getObjectContext(), key, isUserPref, value);
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

}
