package ish.oncourse.portal.components.courseclass;

import ish.common.types.AttendanceType;
import ish.oncourse.model.Attendance;
import ish.oncourse.model.Session;
import ish.oncourse.services.persistence.ICayenneService;
import ish.oncourse.util.FormatUtils;
import org.apache.cayenne.Cayenne;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.util.TextStreamResponse;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static ish.oncourse.portal.services.PortalUtils.DATE_FORMAT_EEEE_dd_MMMMM_h_mma;

/**
 * User: artem
 * Date: 10/16/13
 * Time: 11:16 AM
 */
public class SessionItem {

	private TimeZone timeZone;

	@Inject
	private ICayenneService cayenneService;
	
	private static final String PAST_SESSION = "past-course";
	private static final String FUTURE_SESSION = "future-course";
	private static final String ACTUAL_SESSION = "actual-course";
	private static final String EDIT_ROLL = "Edit Roll";
	private static final String MARK_ROLL = "Mark Roll";
	private static final String VIEW = "View";
	private static final String PAST = "edit-roll past-roll";
	private static final String ACTUAL = "edit-roll actual-roll";
	private static final String FUTURE = "future-roll";


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
		return  FormatUtils.getDateFormat("EEE dd MMM",timeZone).format(session.getStartDate());
	}

	public String getSessionTime(){
		return String.format("%s - %s",
				FormatUtils.getDateFormat("h:mma", timeZone).format(session.getStartDate()),
				FormatUtils.getDateFormat("h:mma", timeZone).format(session.getEndDate()));
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
