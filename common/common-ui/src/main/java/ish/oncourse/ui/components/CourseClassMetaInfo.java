/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.ui.components;

import ish.oncourse.model.CourseClass;
import ish.oncourse.util.FormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;



public class CourseClassMetaInfo {

	@Parameter
	private String format;

	@Inject
	private Request request;


	private CourseClass courseClass;

	@SetupRender
	public void beforeRender() {		
		courseClass = (CourseClass) request.getAttribute(CourseClass.class.getSimpleName());
	}
	
	public String getClassStartDateTime() {

		if (courseClass != null && courseClass.getStartDate() != null) {
			if (StringUtils.trimToNull(format) != null) {
				return FormatUtils.getDateFormat(format, "UTC").format(courseClass.getStartDate());
			} else {
				return FormatUtils.convertDateToISO8601(courseClass.getStartDate());
			}
		}
		return null;
	}

	public String getClassEndDateTime() {

		if (courseClass != null && courseClass.getEndDate() != null) {
			if (StringUtils.trimToNull(format) != null) {
				return FormatUtils.getDateFormat(format, "UTC").format(courseClass.getEndDate());
			} else {
				return FormatUtils.convertDateToISO8601(courseClass.getStartDate());
			}
		}
		return null;
	}
	
}
