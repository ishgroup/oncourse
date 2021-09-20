/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.util;

import ish.oncourse.model.College;
import ish.oncourse.model.Preference;
import ish.oncourse.model.Settings;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;

import java.util.Date;

public class PreferenceUtil {

	public static Preference createPreference(ObjectContext context, College college, String name, String value) {
		Date now = new Date();
		
		Preference pref = context.newObject(Preference.class);

		pref.setCollege(college);
		pref.setName(name);
		pref.setValueString(value);
		pref.setCreated(now);
		pref.setModified(now);

		return pref;
	}
	public static Settings createSetting(ObjectContext context, College college, String name, String value) {
		Date now = new Date();

		Settings setting = context.newObject(Settings.class);

		setting.setCollege(college);
		setting.setName(name);
		setting.setValue(value);
		setting.setCreated(now);
		setting.setModified(now);

		return setting;
	}
}
