/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.admin.utils;

import ish.oncourse.model.College;
import ish.oncourse.model.Preference;
import org.apache.cayenne.ObjectContext;

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
}
