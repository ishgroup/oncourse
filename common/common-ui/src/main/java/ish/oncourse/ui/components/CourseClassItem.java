package ish.oncourse.ui.components;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Room;
import ish.oncourse.model.Session;
import ish.oncourse.model.TutorRole;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.ui.utils.FormatUtils;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.utils.TimestampUtilities;

import java.text.Format;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

public class CourseClassItem {

	@Inject
	private Messages messages;

	@Inject
	private ITextileConverter textileConverter;

	@Inject
	private ICookiesService cookiesService;

	@Parameter
	@Property
	private CourseClass courseClass;

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

	@Property
	private Format dateFormat;

	@Property
	private Format timeFormat;

	@Property
	private List<String> weekdays = TimestampUtilities.DaysOfWeekNames;

	@Property
	private String day;

	@Property
	private int dayIndex;

	@SetupRender
	public void beforeRender() {
		timetableLabels = new ArrayList<String>();
		timetableLabels.add("When");
		timetableLabels.add("Time");
		timetableLabels.add("Where");

		String timeZone = courseClass.getFirstSession() == null ? null : courseClass.getFirstSession().getTimeZone();
		dateFormat = FormatUtils.getShortDateFormat(timeZone);
		timeFormat = FormatUtils.getShortTimeFormat(timeZone);
	}

	public String getCourseClassDetail() {
		String detail = textileConverter.convertCustomTextile(courseClass.getDetail(), new ValidationErrors());
		return detail == null ? "" : detail;
	}

	public boolean isHasSite() {
		return courseClass.getRoom() != null && courseClass.getRoom().getSite() != null;
	}

	public boolean isHasTutorRoles() {
		return courseClass.getTutorRoles().size() > 0;
	}

	public boolean isHasLinkToLocation() {
		return linkToLocationsMap;
	}

	public boolean isNotExistsOnMap() {
		return courseClass.getRoom() == null || courseClass.getRoom().getSite() == null
				|| !courseClass.getRoom().getSite().isHasCoordinates();
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
					siteNameComparison = room1.getSite().getName().compareTo(room2.getSite().getName());
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

		int numberOfSession = courseClass.getSessions().size();
		String key = (numberOfSession > 1) ? "%s sessions, %s hours total" : "%s session, %s hours total";
		return String.format(key, numberOfSession,
				FormatUtils.hoursFormat.format(courseClass.getTotalDurationHours().doubleValue()));
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

	public boolean isLastIndex() {
		return index == courseClass.getTutorRoles().size() - 1;
	}

	public boolean isCurrentClass() {
		return !courseClass.isCancelled() && !courseClass.hasEnded();
	}
	
	public boolean isAddedClass() {
		List<Long> classIds = cookiesService.getCookieCollectionValue(CourseClass.SHORTLIST_COOKIE_KEY, Long.class);
		return classIds.contains(courseClass.getId());
	}

	public String getEnrolHoverTitle() {
		if (courseClass.isCancelled()) {
			return messages.get("message.hover.button.enrol.cancelled");
		}
		if (courseClass.hasEnded()) {
			return messages.get("message.hover.button.enrol.finished");
		}
		if (courseClass.hasStarted()) {
			return messages.get("message.hover.button.enrol.commenced");
		}
		return messages.get("message.hover.button.enrol.current");
	}

	public String getDayKind() {
		return TimestampUtilities.DaysOfWeekendNames.contains(day) ? "end" : "day";
	}

	public String getDayClass() {
		return TimestampUtilities.DaysOfWeekendNames.contains(day) ? "match-9 match-6" : "match-8 match-1";
	}

	public String getHasTiming() {
		Set<String> daysOfWeek = courseClass.getDaysOfWeek();
		return daysOfWeek.contains(day) ? "yes" : "no";
	}

	public String getDayShortName() {
		String shortName = TimestampUtilities.DaysOfWeekAbbreviatedNames.get(dayIndex);
		return shortName.substring(0, shortName.length() - 1);
	}

	public String getDayTimeClass() {
		return courseClass.isDaytime() ? "yes" : "no";
	}

	public String getEveningClass() {
		return courseClass.isEvening() ? "yes" : "no";
	}
}
