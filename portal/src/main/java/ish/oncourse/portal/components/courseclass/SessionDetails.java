/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.components.courseclass;

import ish.common.types.AttendanceType;
import ish.oncourse.model.Attendance;
import ish.oncourse.model.Session;

import ish.oncourse.util.FormatUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

import java.util.Date;
import java.util.TimeZone;

public class SessionDetails {

	@Property
	@Parameter
	private Session session;
	
	@Property
	private boolean marked;
	
	@Property
	private boolean future;

	private TimeZone timeZone;
	
	private static final String MARKED_CLASS = "past-roll-desc";
	private static final String UNMARKED_CLASS = "actual-roll-desc";


	@SetupRender
	boolean setupRender() {
		timeZone = session.getCourseClass().getClassTimeZone();
		marked = Attendance.ATTENDANCE_TYPE.eq(AttendanceType.UNMARKED.getDatabaseValue()).filterObjects(session.getAttendances()).isEmpty();
		future = session.getEndDate().after(new Date());
		return true;
	}
	
	
	public String getMarkedClass() {
		return !future && marked ? MARKED_CLASS : UNMARKED_CLASS;
	}

	public String getSessionDate(){
		return String.format("%s - %s",
				FormatUtils.getDateFormat("EEE dd MMM h:mma", timeZone).format(session.getStartDate()),
				FormatUtils.getDateFormat("h:mma", timeZone).format(session.getEndDate()));
	}
	
}
