/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.services.courseclass;

import ish.oncourse.model.College;
import ish.oncourse.services.preference.GetPreference;
import ish.oncourse.services.preference.Preferences;

/**
 * User: akoiro
 * Date: 15/6/18
 */
public class GetHideOnWebClassAge {
	private College college;

	public GetHideOnWebClassAge college(College college) {
		this.college = college;
		return this;
	}

	public ClassAge get() {
		String age = new GetPreference(college, Preferences.HIDE_CLASS_ON_WEB_AGE, college.getObjectContext()).getValue();
		String type = new GetPreference(college, Preferences.HIDE_CLASS_ON_WEB_AGE_TYPE, college.getObjectContext()).getValue();
		return ClassAge.valueOf(age, type);
	}
}
