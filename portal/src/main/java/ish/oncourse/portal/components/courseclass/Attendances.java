/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.components.courseclass;

import ish.oncourse.model.Attendance;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.Session;
import ish.oncourse.portal.services.attendance.AttendanceUtils;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

import java.util.List;

public class Attendances {

	@Parameter
	@Property
	private Enrolment enrolment;


	@Property
	private Attendance attendance;

	@Property
	private List<Attendance> attendances;

	@SetupRender
	boolean setupRender() {
		attendances = searchSessionBy(enrolment);
		return true;
	}

	public int getNumberOfSessions(){
		return attendances.size();
	}

	public boolean isShowSessions() {
		return !attendances.isEmpty();
	}

	/**
	 *  The method searches Sessions for the  courseClass and sorts them by startDate field.
	 *  The method has been introduced because we need sorting by startDate field, CourseClass.getSessions()
	 */
	private List<Attendance> searchSessionBy(Enrolment enrolment) {
		return ObjectSelect.query(Attendance.class)
				.where(Attendance.SESSION.dot(Session.COURSE_CLASS).eq(enrolment.getCourseClass()))
				.and(Attendance.STUDENT.eq(enrolment.getStudent()))
				.orderBy(Attendance.SESSION.dot(Session.START_DATE).asc())
				.select(enrolment.getObjectContext());
	}
	
	public Integer getAttendancePercent() {
		return AttendanceUtils.getAttendancePercent(enrolment);
	}
}
