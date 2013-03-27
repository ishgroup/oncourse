package ish.oncourse.ui.pages;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Session;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.util.HTMLUtils;
import ish.oncourse.util.ValidationErrors;
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
			public int compare(Session s1, Session s2) {

				// sorting sessions by their start date, if start dates are equal then sorting by site name

				if (s1.getStartDate().equals(s2.getStartDate())) {

					if (s1.getRoom() != null && s1.getRoom().getSite() != null
							&& s2.getRoom() != null && s2.getRoom().getSite() != null) {

						return s1.getRoom().getSite().getName().compareTo(
								s2.getRoom().getSite().getName());
					}
				}

				return s1.getStartDate().compareTo(s2.getStartDate());
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

	public String getCanonicalLinkPath() {
		return HTMLUtils.getCanonicalLinkPathFor(courseClass.getCourse(), request);
	}

}
