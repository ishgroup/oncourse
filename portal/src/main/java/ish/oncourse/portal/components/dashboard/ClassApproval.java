/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.components.dashboard;

import ish.oncourse.model.CourseClass;
import ish.oncourse.util.FormatUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

public class ClassApproval {
	
	@Parameter
	@Property
	private CourseClass classToApproval;
	
	public String getFormattedClassStartDate() {
		return FormatUtils.getDateFormat("EEE d MMM h:mma", classToApproval.getTimeZone()).format(classToApproval.getStartDate());
	}

}
