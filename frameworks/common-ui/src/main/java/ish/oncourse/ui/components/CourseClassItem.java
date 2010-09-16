package ish.oncourse.ui.components;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Room;
import ish.oncourse.model.Session;
import ish.oncourse.model.TutorRole;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

public class CourseClassItem {
	@Parameter
	@Property
	private CourseClass courseClass;

	@Parameter
	@Property
	private boolean excludePrice;

	@Parameter
	@Property
	private boolean excludeShortlistControl;

	@Property
	private TutorRole tutorRole;

	@Property
	private int index;

	private Format hoursFormat;

	private List<String> timetableLabels;

	@Parameter
	@Property
	private boolean isList;

	@Parameter
	@Property
	private boolean linkToLocationsMap;

	@SetupRender
	public void beforeRender() {
		this.hoursFormat = new DecimalFormat("0.#");
		timetableLabels = new ArrayList<String>();
		timetableLabels.add("When");
		timetableLabels.add("Time");
		timetableLabels.add("Where");
	}

	public boolean isHasSite() {
		return courseClass.getRoom() != null
				&& courseClass.getRoom().getSite() != null;
	}

	public boolean isHasTutorRoles() {
		return courseClass.getTutorRoles().size() > 0;
	}

	public boolean isHasLinkToLocation() {
		return linkToLocationsMap && isList;
	}

	public boolean isHasSiteSuburb() {
		Room room = courseClass.getRoom();
		return room != null
				&& room.getSite() != null
				&& room.getSite().getSuburb() != null
				&& !"".equals(room.getSite().getSuburb());
	}

	public boolean isTutorPortal() {
		// if ( context().page() instanceof TutorClasses )
		// {
		// return ( ( TutorClasses )context().page() ).isTutor();
		// }
		// return false;
		return false;
	}

	public String getSessionForClass() {
		return "sessions_for_class" + (isTutorPortal() ? "" : " hidden");
	}

	public Format getHoursFormat() {
		return this.hoursFormat;
	}

	public boolean isHasSiteName() {
		return isHasSite() && courseClass.getRoom().getSite().getName() != null
				&& !"online".equals(courseClass.getRoom().getSite().getName());
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
