package ish.oncourse.ui.pages;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Session;
import ish.oncourse.services.courseclass.ICourseClassService;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CourseClassDetails {

	@Inject
	private Request request;
	
	@Inject
	private ICourseClassService courseClassService;

	@Property
	private CourseClass courseClass;

	private List<String> timetableLabels;

	@SetupRender
	public void beforeRender() {
		String code = (String) request.getAttribute("courseClassCode");
		courseClass = courseClassService.getCourseClassByFullCode(code);
		
		timetableLabels = new ArrayList<String>();
		timetableLabels.add("When");
		timetableLabels.add("Time");
		timetableLabels.add("Where");
	}

	public String getCourseDetail() {
		String detail = courseClass.getCourse().getDetail();
		return detail == null ? "" : detail;
	}

	public String getCourseClassDetail() {
		String detail = courseClass.getDetail();
		return detail == null ? "" : detail;
	}

	public boolean isShowInlineTimetable() {
		/*
		 * if ( this.showInlineTimetable == null ) { if ( hasBinding(
		 * "showInlineTimetable" ) ) { this.showInlineTimetable =
		 * booleanForBinding( "showInlineTimetable" ); } else if ( !hasObject()
		 * || !Boolean.TRUE.equals( getObject().college().preferenceForKey(
		 * "courseclass.sessions.timetable.asinline" ) ) ) {
		 * this.showInlineTimetable = true; } if ( this.showInlineTimetable ==
		 * null ) { this.showInlineTimetable = false; } } return
		 * this.showInlineTimetable;
		 */
		return true;
	}

	public List<String> getTimetableLabels() {
		return timetableLabels;
	}

	public List<Session> getSortedTimelineableSessions() {
		List<Session> sessions = courseClass.getTimelineableSessions();
		Collections.sort(sessions, new Comparator<Session>() {
			public int compare(Session o1, Session o2) {
				int siteNameComparison = o1.getRoom().getSite().getName()
						.compareTo(o2.getRoom().getSite().getName());
				if (siteNameComparison == 0) {
					return o1.getStartDate().compareTo(o2.getStartDate());
				}
				return siteNameComparison;
			}
		});
		return sessions;
	}

	public String getCssTableClass() {
		return "session-table";
	}

	public String getCssEvenRowClass() {
		return "tr-even";
	}

	public String getCssOddRowClass() {
		return "tr-odd";
	}

	public String getDateFormat() {
		return "EEE dd MMM yyyy";
	}

	public String getTimeFormat() {
		return "hh:mma";
	}

	public String getTimedateClass() {
		return "class_timedate"
				+ (courseClass.isHasSessions() ? " tooltip" : "");
	}
}
