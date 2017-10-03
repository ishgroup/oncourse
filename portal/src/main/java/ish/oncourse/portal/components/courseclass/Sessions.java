package ish.oncourse.portal.components.courseclass;

import ish.common.types.AttendanceType;
import ish.oncourse.model.AttachmentType;
import ish.oncourse.model.Attendance;
import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Session;
import ish.oncourse.model.WebMenu;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.util.FormatUtils;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.Ordering;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.util.TextStreamResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static ish.oncourse.portal.services.PortalUtils.DATE_FORMAT_EEEE_dd_MMMMM_h_mma;

/**
 * User: artem
 * Date: 10/16/13
 * Time: 11:06 AM
 */
public class Sessions {

	@Parameter
	@Property
	private CourseClass courseClass;


	@Property
	private Session session;

	@Property
	private List<Session> sessions;

	@SetupRender
	boolean setupRender() {
		sessions = searchSessionBy(courseClass);
		return true;
	}
	
	public int getNumberOfSessions(){
		return sessions.size();
	}

	public boolean isShowSessions() {
		return !sessions.isEmpty();
	}
	
	/**
	 *  The method searches Sessions for the  courseClass and sorts them by startDate field.
	 *  The method has been introduced because we need sorting by startDate field, CourseClass.getSessions()
	 */
	private List<Session> searchSessionBy(CourseClass courseClass) {
		return ObjectSelect.query(Session.class)
				.where(Session.COURSE_CLASS.eq(courseClass))
				.orderBy(Session.START_DATE.asc())
				.prefetch(Session.ATTENDANCES.disjoint())
				.select(courseClass.getObjectContext());
	}
}
