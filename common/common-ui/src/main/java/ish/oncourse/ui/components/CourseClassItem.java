package ish.oncourse.ui.components;

import ish.oncourse.model.CourseClass;
import ish.oncourse.model.Room;
import ish.oncourse.model.Session;
import ish.oncourse.model.TutorRole;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.util.FormatUtils;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.utils.TimestampUtilities;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.text.Format;
import java.util.*;

public class CourseClassItem {

    private static final String VALUE_yes = "yes";
    private static final String VALUE_no = "no";

    @Inject
	private Messages messages;

	@Inject
	private ITextileConverter textileConverter;

	@Inject
	private ICookiesService cookiesService;
	
	@Inject
	private PreferenceController preferenceController;

	@Parameter
	@Property
	private CourseClass courseClass;

	@SuppressWarnings("all")
	@Property
	private TutorRole tutorRole;

	@Property
	private int index;

	private List<String> timetableLabels;

	@SuppressWarnings("all")
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
	private Format timeFormatWithTimeZone;

	@SuppressWarnings("all")
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

		TimeZone timeZone = getClientTimeZone();
		dateFormat = FormatUtils.getDateFormat(FormatUtils.shortDateFormatString, timeZone);
		timeFormat = FormatUtils.getDateFormat(FormatUtils.shortTimeFormatString, timeZone);
		timeFormatWithTimeZone = FormatUtils.getDateFormat(FormatUtils.timeFormatWithTimeZoneString, timeZone);
	}

	public TimeZone getClientTimeZone() {

		Integer offset = cookiesService.getClientTimezoneOffset();
		if (offset == null)
			return TimeZone.getTimeZone(courseClass.getTimeZone());
		else
		{
			offset = offset * 60000;
			return new SimpleTimeZone(offset, "GMT");
		}
	}

	public String getCourseClassDetail() {
		String detail = textileConverter.convertCustomTextile(courseClass.getDetail(), new ValidationErrors());
		return detail == null ? StringUtils.EMPTY : detail;
	}
	
	public List<TutorRole> getVisibleTutorRoles() {
		final List<TutorRole> visibleRoles = new ArrayList<TutorRole>();
		for (TutorRole role : courseClass.getTutorRoles()) {
			if (role.getInPublicity()) {
				visibleRoles.add(role);
			}
		}
		return visibleRoles;
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
		return courseClass.isHasRoom() && courseClass.getRoom().getSite().getName() != null
				&& !"online".equals(courseClass.getRoom().getSite().getName());
	}

	public List<String> getTimetableLabels() {
		return timetableLabels;
	}

	public List<Session> getSortedTimelineableSessions() {
		List<Session> sessions = courseClass.getTimelineableSessions();
		Collections.sort(sessions, new Comparator<Session>() {
			public int compare(Session o1, Session o2) {
				int siteNameComparison = o1.getStartDate().compareTo(o2.getStartDate());
				Room room1 = o1.getRoom();
				Room room2 = o2.getRoom();
				if (siteNameComparison == 0 && room1 != null && room2 != null) {
					siteNameComparison = room1.getSite().getName().compareTo(room2.getSite().getName());
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
	
	public String getExpectedHours() {
		return (courseClass.getIsDistantLearningCourse() && courseClass.getExpectedHours() != null) ? 
				String.format("Approximately %.0f hours", courseClass.getExpectedHours().doubleValue()) : StringUtils.EMPTY;
	}
	
	public String getMaximumDaysToComplete() {
		return (courseClass.getIsDistantLearningCourse() && courseClass.getMaximumDays() != null ? 
			String.format("%.0f maximum days to complete", courseClass.getMaximumDays().doubleValue()) : StringUtils.EMPTY);
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
		return !courseClass.isCancelled() && (!courseClass.hasEnded() || courseClass.getIsDistantLearningCourse());
	}
	
	public boolean isFinishedClass() {
		return !courseClass.isCancelled() && courseClass.hasEnded();
	}
	
	public boolean isHasAvailableEnrolmentPlaces() {
		return courseClass != null && courseClass.isHasAvailableEnrolmentPlaces();
	}
	
	public boolean isPaymentGatewayEnabled() {
		return courseClass != null && preferenceController.isPaymentGatewayEnabled();
	}
	
	public boolean isHasManySessions() {
		return courseClass != null && courseClass.isHasManySessions() && courseClass.isSessionsHaveDifferentTimes();
	}

	public boolean isAddedClass() {
		List<Long> classIds = cookiesService.getCookieCollectionValue(CourseClass.SHORTLIST_COOKIE_KEY, Long.class);
		return classIds.contains(courseClass.getId());
	}
	
	public boolean isSelfPacedClass() {
		return courseClass.getIsDistantLearningCourse();
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
		return daysOfWeek.contains(day) ? VALUE_yes : VALUE_no;
	}

	public String getDayShortName() {
		String shortName = TimestampUtilities.DaysOfWeekAbbreviatedNames.get(dayIndex);
		return shortName.substring(0, shortName.length() - 1);
	}

	public String getDayTimeClass() {
		return courseClass.isDaytime() ? VALUE_yes : VALUE_no;
	}

	public String getEveningClass() {
		return courseClass.isEvening() ? VALUE_yes : VALUE_no;
	}

    /**
     * The method returns true only if start date and end date for the courseClass more than 1 day.
     */
    public boolean isShowDateEnd()
    {
        Date endDate = courseClass.getEndDate();
        List<Session> sessions = courseClass.getSessions();

        if (endDate == null || sessions.isEmpty())
            return false;

        for (Session session : sessions) {
            if (!DateUtils.isSameDay(endDate, session.getStartDate()))
            {
                return true;
            }
        }
        return false;
    }
}
