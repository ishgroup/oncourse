/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.services;

import ish.oncourse.model.CourseClass;

import java.util.Date;

public class PCourseClass {

	private CourseClass courseClass;
	private Date startDate;
	private Date endDate;

	public PCourseClass(CourseClass courseClass, Date startDate, Date endDate) {
		this.courseClass = courseClass;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public CourseClass getCourseClass() {
		return courseClass;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PCourseClass) {
			return ((PCourseClass) obj).courseClass == this.courseClass;
		}
		else {
			return false;
		}
	}
}
