/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.services.attendance

import ish.common.types.AttendanceType
import ish.oncourse.model.Enrolment
import ish.oncourse.portal.services.IPortalService

public class SessionResponse {
	
	public static final String PAST_SESSION = "past-course";
	public static final String FUTURE_SESSION = "future-course";
	public static final String ACTUAL_SESSION = "actual-course";
	public static final String EDIT_ROLL = "Edit Roll";
	public static final String MARK_ROLL = "Mark Roll";
	public static final String VIEW = "View";
	public static final String PAST = "edit-roll past-roll";
	public static final String ACTUAL = "edit-roll actual-roll";
	public static final String FUTURE = "future-roll";

	def Integer percent
	def String timeClass
	def String labelText
	def String labelClass
	def Long sessionId
	def Long studentId

	public static SessionResponse valueOf(ish.oncourse.model.Attendance att, IPortalService portalService) {
		SessionResponse response = new SessionResponse().with {

			Enrolment enrolment = portalService.getEnrolmentBy(att.getStudent(), att.getSession().getCourseClass());
			it.percent =  AttendanceUtils.getAttendancePercent(enrolment);
			it.sessionId = att.session.id
			it.studentId = att.student.id

			def marked = ish.oncourse.model.Attendance.ATTENDANCE_TYPE.eq(AttendanceType.UNMARKED.getDatabaseValue()).filterObjects(att.session.attendances).isEmpty();
			
			if (marked) {
				it.timeClass = PAST_SESSION
				it.labelText = EDIT_ROLL
				it.labelClass = PAST
			} else {
				it.timeClass = ACTUAL_SESSION
				it.labelText = MARK_ROLL
				it.labelClass = ACTUAL
			}
			return it
		}
		return response
	}
}
