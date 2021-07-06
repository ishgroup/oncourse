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
package ish.oncourse.server.scripting.api;

import com.google.inject.Inject;
import ish.oncourse.API;
import ish.oncourse.server.PreferenceController;
import ish.oncourse.server.cayenne.Student;
import ish.oncourse.server.license.LicenseService;
import ish.persistence.Preferences;
import ish.util.UrlUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * Service for accessing college preferences.
 *
 */
@API
public class CollegePreferenceService {

	public static final String PREFERENCE_ALIAS = "preference";

	private static final int DEFAULT_PORTAL_URL_SIGNATURE_TIMEOUT = 7; // 6 days, link expires at 12am on the seventh day

	private PreferenceController preferenceController;
	private PreferenceHelper preferenceHelper;
	private LicenseService licenseService;

	private static final String HTTP_PREFIX = "https";
	private static final String PROTOCOL_DELIMITER = "://";


	@Inject
	public CollegePreferenceService(PreferenceController preferenceController, LicenseService licenseService) {
		this.preferenceController = preferenceController;
		this.preferenceHelper = new PreferenceHelper(preferenceController);
		this.licenseService = licenseService;
	}

	/**
	 * Returns preference value.
	 */
	public Object getValue(String prefName) {
		return preferenceController.getValueForKey(prefName);
	}

	/**
	 * @param prefName name of preference
	 * @param defaultValue default value
	 *
	 * @return preference value if it is not null, otherwise returns default value
	 */
	@API
	public Object get(String prefName, Object defaultValue) {
		var value = preferenceController.getValueForKey(prefName);


		// workaround for adding "Http://" prefix to colleges web url if it was not specified properly.
		// Do it exactly here, because it needs for each email template
		if(Preferences.COLLEGE_URL.equals(prefName) && value != null && !((String)value).contains(PROTOCOL_DELIMITER)) {
			value = String.format("%s%s%s", HTTP_PREFIX, PROTOCOL_DELIMITER, value);
		}

		return value != null ? value : defaultValue;
	}

	/**
	 * @param prefName name of preference
	 *
	 * @return preference value if it is not null, otherwise returns empty string.
	 */
	@API
	public Object get(String prefName) {
		return get(prefName, StringUtils.EMPTY);
	}

	/**
	 * Returns link to USI details entering page in portal crafted for a specific student
	 * and valid for 7 days.
	 *
	 * @param student student whose USI details will be collected
	 * @return link to USI details entering page in portal
	 * @deprecated As of onCourse version 8.12,
	 * replaced by Student.getPortalLink("USI").
	 */
	@Deprecated
	public String getPortalCensusUrl(Student student) {
		return getPortalCensusUrl(student, DEFAULT_PORTAL_URL_SIGNATURE_TIMEOUT);
	}

	/**
	 * Returns link to USI details entering page in portal crafted for a specific student
	 * and valid for specific number days.
	 *
	 * @param student student whose USI details will be collected
	 * @param expiryDays is url timeout
	 * @return link to USI details entering page in portal
	 * @deprecated As of onCourse version 8.12,
	 * replaced by Student.getPortalLink("USI", Object timeout).
	 */
	@Deprecated
	public String getPortalCensusUrl(Student student, int expiryDays) {
		var expiryDate = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
		expiryDate = DateUtils.addDays(expiryDate, expiryDays);

		return UrlUtil.createPortalUsiLink(
				student.getContact().getUniqueCode(), expiryDate, licenseService.getSecurity_key());
	}

	/**
	 * Generates signed URL granting access to specific page in portal for a specific number of days.
	 * For example following statement
	 *
	 * restrictedPortalURL("survey/1531", 10)
	 *
	 * if executed on 1 Jan 2016 will yield the following URL:
	 *
	 * https://www.skillsoncourse.com.au/portal/survey/1531?valid=20160111&key=k9_S8uk68W5PoCvq5lSUp70sqQY
	 *
	 * @param path partial portal URL path which will be prepended with 'https://skillsoncourse.com.au/portal'
	 * @param expiryDays number of days after which URL will be expired and no longer valid
	 * @return signed link to specific portal page
	 * @deprecated As of onCourse version 8.12,
	 * replaced by Student.getPortalLink(Object target, Object timeout).
	 */
	@Deprecated
	public String restrictedPortalURL(String path, int expiryDays) {
		var expiryDate = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
		expiryDate = DateUtils.addDays(expiryDate, expiryDays);

		return UrlUtil.createSignedPortalUrl(path, expiryDate, licenseService.getSecurity_key());
	}

	/**
	 * Generates signed URL granting access to specific page in portal for 7 days.
	 * For example following statement
	 *
	 * restrictedPortalURL("survey/1531")
	 *
	 * if executed on 1 Jan 2016 will yield the following URL:
	 *
	 * https://www.skillsoncourse.com.au/portal/survey/1531?valid=20160108&key=k9_S8uk68W5PoCvq5lSUp70sqQY
	 *
	 * @param path partial portal URL path which will be prepended with 'https://skillsoncourse.com.au/portal'
	 * @return signed link to specific portal page
	* @deprecated As of onCourse version 8.12,
	 * replaced by Student.getPortalLink(Object target).
	 */
	@Deprecated
	public String restrictedPortalURL(String path) {
		return restrictedPortalURL(path, DEFAULT_PORTAL_URL_SIGNATURE_TIMEOUT);
	}

	public Object getPrefHelper() {
		return preferenceHelper.pref();
	}
}
