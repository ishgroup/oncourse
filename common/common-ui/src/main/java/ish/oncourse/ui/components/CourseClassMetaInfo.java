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
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class CourseClassMetaInfo {

	@Parameter
	private String format;

	@Inject
	private Request request;


	private CourseClass courseClass;

	private static final String DEFAULT_FORMAT= "yyyy-MM-dd'T'HH:mm:ssZZ";
	private DateTimeFormatter dtf;
	
	@SetupRender
	public void beforeRender() {
		if (StringUtils.trimToNull(format) != null) {
			dtf = DateTimeFormat.forPattern(format).withZone(DateTimeZone.forTimeZone(courseClass.getClassTimeZone()));
		} else {
			dtf = DateTimeFormat.forPattern(DEFAULT_FORMAT);
		}
		courseClass = (CourseClass) request.getAttribute(CourseClass.class.getSimpleName());
	}
	
	public String getClassStartDateTime() {

		if (courseClass != null && courseClass.getStartDate() != null) {
			return dtf.print(courseClass.getStartDate().getTime());
		}
		return null;
	}

	public String getClassEndDateTime() {

		if (courseClass != null && courseClass.getEndDate() != null) {
			return dtf.print(courseClass.getStartDate().getTime());
		}
		return null;
	}
	
}
