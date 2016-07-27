package ish.oncourse.portal.components.courseclass;

import ish.common.types.AttendanceType;
import ish.oncourse.model.Attendance;
import ish.oncourse.model.Session;
import ish.oncourse.portal.services.attendance.AttendanceUtils;
import ish.oncourse.services.persistence.ICayenneService;
import static ish.oncourse.portal.services.attendance.SessionResponse.*;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.Date;
import java.util.TimeZone;

/**
 * User: artem
 * Date: 10/16/13
 * Time: 11:16 AM
 */
public class SessionItem {

	private TimeZone timeZone;

	@Inject
	private ICayenneService cayenneService;
	
	@Property
	@Parameter
	private Session session;

	private boolean marked;

	private boolean future;

	@SetupRender
	boolean setupRender() {
		timeZone = session.getCourseClass().getClassTimeZone();
		marked = Attendance.ATTENDANCE_TYPE.eq(AttendanceType.UNMARKED.getDatabaseValue()).filterObjects(session.getAttendances()).isEmpty();
		future = session.getEndDate().after(new Date());

		return true;
	}

	public String getStartDate(){
		return AttendanceUtils.getStartDate(timeZone, session);
	}

	public String getSessionTime(){
		return AttendanceUtils.getSessionTime(timeZone, session);
	}
	
	public String getTimeClass() {
		return future ? FUTURE_SESSION : marked ? PAST_SESSION : ACTUAL_SESSION ;
	}

	public String getLableText() {
		return future ? VIEW : marked ? EDIT_ROLL : MARK_ROLL;
	}

	public String getLableClass() {
		return  future ? FUTURE : marked ? PAST : ACTUAL;
	}

}
