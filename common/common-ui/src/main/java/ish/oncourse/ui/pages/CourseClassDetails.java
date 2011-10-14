package ish.oncourse.ui.pages;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Room;
import ish.oncourse.model.Session;
import ish.oncourse.model.Site;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.util.ValidationErrors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class CourseClassDetails {

	@Inject
	private ITextileConverter textileConverter;

	@Inject
	private Request request;

	@Property
	private CourseClass courseClass;

	private List<String> timetableLabels;

	@SetupRender
	public void beforeRender() {
		courseClass = (CourseClass) request.getAttribute(CourseClass.class.getSimpleName());

		timetableLabels = new ArrayList<String>();
		timetableLabels.add("When");
		timetableLabels.add("Time");
		timetableLabels.add("Where");
	}

	public String getCourseDetail() {
		String detail = textileConverter.convertCustomTextile(courseClass.getCourse().getDetail(),
				new ValidationErrors());
		return detail == null ? "" : detail;
	}

	public String getCourseClassDetail() {
		String detail = textileConverter.convertCustomTextile(courseClass.getDetail(), new ValidationErrors());
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
				String location1 = "";
				String location2 = "";
				Room room1 = o1.getRoom();
				Room room2 = o2.getRoom();

				if (room1 != null) {
					Site site1 = room1.getSite();
					if (site1 != null) {
						location1 = site1.getName();
					}
				}

				if (room2 != null) {
					Site site2 = room2.getSite();
					if (site2 != null) {
						location2 = site2.getName();
					}
				}

				int siteNameComparison = location1.compareTo(location2);
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

	public String getTimedateClass() {
		return "class_timedate" + (courseClass.isHasSessions() ? " tooltip" : "");
	}
}
