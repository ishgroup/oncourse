package ish.oncourse.ui.components;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Room;
import ish.oncourse.model.Session;
import ish.oncourse.model.Site;
import ish.oncourse.model.TutorRole;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

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

	private List<String> timetableLabels;

	@Parameter
	@Property
	private boolean isList;

	@Parameter
	@Property
	private boolean linkToLocationsMap;
	
	@Inject
	private Messages messages;

	@SetupRender
	public void beforeRender() {
		timetableLabels = new ArrayList<String>();
		timetableLabels.add("When");
		timetableLabels.add("Time");
		timetableLabels.add("Where");
	}

	public boolean isHasSite() {
		return courseClass.getRoom() != null && courseClass.getRoom().getSite() != null;
	}

	public boolean isHasTutorRoles() {
		return courseClass.getTutorRoles().size() > 0;
	}

	public boolean isHasLinkToLocation() {
		return linkToLocationsMap && isList;
	}

	public boolean isExistsOnMap() {
		Room room = courseClass.getRoom();
		if (room == null) {
			return false;
		}
		Site site = room.getSite();
		if (site == null) {
			return false;
		}

		return site.isHasCoordinates();

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
				int siteNameComparison = 0;
				Room room1 = o1.getRoom();
				Room room2 = o2.getRoom();
				if (room1 != null && room2 != null) {
					siteNameComparison = room1.getSite().getName()
							.compareTo(room2.getSite().getName());
				}
				if (siteNameComparison == 0) {
					return o1.getStartDate().compareTo(o2.getStartDate());
				}
				return siteNameComparison;
			}
		});
		return sessions;
	}
	
	public String getClassSessions() {
		DecimalFormat hoursFormat = new DecimalFormat("0.#");
		int numberOfSession = courseClass.getSessions().size();
		String key = (numberOfSession > 1) ? "session.number" : "one.session";
		return messages.format(key, numberOfSession, hoursFormat.format(courseClass.getTotalDurationHours().doubleValue()));
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
		return "class_timedate" + (courseClass.isHasSessions() ? " tooltip" : "");
	}
}
