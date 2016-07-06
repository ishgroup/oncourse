/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.components.courseclass;

import ish.common.types.AttendanceType;
import ish.common.types.TypesUtil;
import ish.oncourse.model.Attendance;
import ish.oncourse.portal.services.attendance.AttendanceUtils;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.Date;
import java.util.TimeZone;

public class AttendanceItem {
	private TimeZone timeZone;

	@Inject
	private ICayenneService cayenneService;

	private static final String PAST_SESSION = "past-course";
	private static final String FUTURE_SESSION =  "future-course";
	
	private static final String UNMARKED_SESSION =  " actual-course";
	private static final String FAILED_SESSION =  " failed-course";


	private static final String UNMARKED ="glyphicon-th-list";
	private static final String OK_MARKED ="glyphicon-ok";
	private static final String NO_MARKED ="glyphicon-remove";


	@Property
	@Parameter
	private Attendance attendance;

	private boolean marked;

	private boolean future;

	@SetupRender
	boolean setupRender() {
		timeZone = attendance.getSession().getCourseClass().getClassTimeZone();
		marked = Attendance.ATTENDANCE_TYPE.eq(AttendanceType.UNMARKED.getDatabaseValue()).filterObjects(attendance.getSession().getAttendances()).isEmpty();
		future = attendance.getSession().getEndDate().after(new Date());

		return true;
	}

	public String getStartDate(){
		return AttendanceUtils.getStartDate(timeZone, attendance.getSession());
	}

	public String getSessionTime(){
		return AttendanceUtils.getSessionTime(timeZone, attendance.getSession());
	}

	public String getTimeClass() {
		String clazz =  future ? FUTURE_SESSION : PAST_SESSION;
		
		switch (TypesUtil.getEnumForDatabaseValue(attendance.getAttendanceType(), AttendanceType.class)) {
			case UNMARKED:
				clazz = clazz.concat(UNMARKED_SESSION);
			case DID_NOT_ATTEND_WITH_REASON:
			case DID_NOT_ATTEND_WITHOUT_REASON:
				clazz = clazz.concat(FAILED_SESSION);
		}
		
		return clazz;
	}

	public String getAttendedClass() {
		switch (TypesUtil.getEnumForDatabaseValue(attendance.getAttendanceType(), AttendanceType.class)) {
			case PARTIAL:
			case ATTENDED:
				return OK_MARKED;
			case UNMARKED:
				return UNMARKED;
			case DID_NOT_ATTEND_WITH_REASON:
			case DID_NOT_ATTEND_WITHOUT_REASON:
				return NO_MARKED;
			default: throw new IllegalArgumentException();
		}
	}

}
