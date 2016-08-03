package ish.oncourse.portal.components.courseclass;

import ish.oncourse.model.Session;
import ish.oncourse.portal.services.attendance.AttendanceUtils;
import ish.oncourse.portal.services.attendance.SessionStyle;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.TimeZone;

import static ish.oncourse.portal.services.attendance.SessionResponse.*;

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

	@Property
	private String timeClass;

	@Property
	private String labelText;

	@Property
	private String labelClass;

	@SetupRender
	boolean setupRender() {
		timeZone = session.getCourseClass().getClassTimeZone();
		SessionStyle style = SessionStyle.valueOf(session);
		switch (style) {
			case empty:
				timeClass = PAST_SESSION;
				labelText = VIEW;
				labelClass = FUTURE;
				break;
			case marked:
				timeClass = PAST_SESSION;
				labelText = EDIT_ROLL;
				labelClass = PAST;
				break;
			case future:
				timeClass = FUTURE_SESSION;
				labelText = VIEW;
				labelClass = FUTURE;
				break;
			case unmarked:
				timeClass = ACTUAL_SESSION;
				labelText = MARK_ROLL;
				labelClass = ACTUAL;
				break;
			default:
				throw new IllegalArgumentException();
		}
		return true;
	}

	public String getStartDate(){
		return AttendanceUtils.getStartDate(timeZone, session);
	}

	public String getSessionTime(){
		return AttendanceUtils.getSessionTime(timeZone, session);
	}
}
