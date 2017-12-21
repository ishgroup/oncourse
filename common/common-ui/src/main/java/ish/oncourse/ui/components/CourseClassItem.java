package ish.oncourse.ui.components;

import ish.math.Money;
import ish.oncourse.model.*;
import ish.oncourse.services.cookies.ICookiesService;
import ish.oncourse.services.courseclass.CheckClassAge;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.services.preference.PreferenceController;
import ish.oncourse.services.textile.ITextileConverter;
import ish.oncourse.services.tutor.ITutorService;
import ish.oncourse.ui.base.ISHCommon;
import ish.oncourse.ui.utils.CourseContext;
import ish.oncourse.util.CustomizedDateFormat;
import ish.oncourse.util.FormatUtils;
import ish.oncourse.util.ValidationErrors;
import ish.oncourse.utils.SessionUtils;
import ish.oncourse.utils.TimestampUtilities;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.QueryCacheStrategy;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.text.Format;
import java.util.*;

import static ish.oncourse.utils.SessionUtils.StartEndTime;
public class CourseClassItem extends ISHCommon {

    private static final String VALUE_yes = "yes";
    private static final String VALUE_no = "no";

	private static final String CLASS_NAME_classCommenced = "classCommenced";

    @Inject
	private Messages messages;

	@Inject
	private ITextileConverter textileConverter;

	@Inject
	private ICookiesService cookiesService;

	@Inject
	private PreferenceController preferenceController;

	@Inject
	private ICourseClassService courseClassService;

    @Inject
    private ITutorService tutorService;

	@Parameter
	@Property
	private CourseClass courseClass;

	@SuppressWarnings("all")
	@Property
	private TutorRole tutorRole;

	@Property
	private int index;

	@SuppressWarnings("all")
	@Parameter
	@Property
	private boolean isList;

	@Parameter
	@Property
	private boolean linkToLocationsMap;

	@Property
	private Format startDateFormat;

	@Property
	private Format endDateFormat;

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

	@Property
	private List<StartEndTime> sessionDays;

	@Parameter
	@Property
	private boolean allowByAplication;

	@Parameter
	@Property
	private Money feeOverride;

	@Property
	private List<TutorRole> visibleTutorRoles;
	
	private List<Session> timelineableSessions;

	private CourseContext context;

	@SetupRender
	public void beforeRender() {

        TimeZone timeZone = courseClassService.getClientTimeZone(courseClass);
		context = (CourseContext) request.getAttribute(CourseItem.COURSE_CONTEXT);

		endDateFormat =  FormatUtils.getDateFormat(FormatUtils.dateFormatString, timeZone);
		startDateFormat = needFormatWithoutYear() ? FormatUtils.getDateFormat(FormatUtils.DATE_FORMAT_EEE_dd_MMM, timeZone) :
				FormatUtils.getDateFormat(FormatUtils.dateFormatString, timeZone);

		timeFormat = new CustomizedDateFormat(FormatUtils.shortTimeFormatString, timeZone);

		if (timeZone.getRawOffset() == TimeZone.getTimeZone(courseClass.getCollege().getTimeZone()).getRawOffset())
			timeFormatWithTimeZone = new CustomizedDateFormat(FormatUtils.shortTimeFormatString, timeZone);
		else
			timeFormatWithTimeZone = new CustomizedDateFormat(FormatUtils.timeFormatWithTimeZoneString, timeZone);

		timelineableSessions = courseClass.getPersistentTimelineableSessions();
		sessionDays = SessionUtils.getSessionDays(timelineableSessions);
		initVisibleTutorRoles();
	}

	public boolean isHasSessionsInTheSameDay() {
		return sessionDays.size() != timelineableSessions.size();
	}

	public Date getFirstSessionStartDate() {
		return isHasSessionsInTheSameDay() ? new Date(sessionDays.get(0).getStartTime()) : courseClass.getFirstSession().getStartDate();
	}

	public Date getFirstSessionEndDate() {
		return isHasSessionsInTheSameDay() ? new Date(sessionDays.get(0).getEndTime()) : courseClass.getFirstSession().getEndDate();
	}

	public String getCourseClassDetail() {
		String detail = textileConverter.convertCustomTextile(courseClass.getDetail(), new ValidationErrors());
		return detail == null ? StringUtils.EMPTY : detail;
	}

	private void initVisibleTutorRoles() {
		visibleTutorRoles = ObjectSelect.query(TutorRole.class)
				.where(TutorRole.COURSE_CLASS.eq(courseClass))
				.and(TutorRole.IN_PUBLICITY.isTrue())
				.and(TutorRole.TUTOR.dot(Tutor.FINISH_DATE).isNull().orExp(TutorRole.TUTOR.dot(Tutor.FINISH_DATE).gt(new Date())))
				.prefetch(TutorRole.TUTOR.joint())
				.prefetch(TutorRole.TUTOR.dot(Tutor.CONTACT).joint())
				.cacheStrategy(QueryCacheStrategy.LOCAL_CACHE, TutorRole.class.getSimpleName())
				.select(courseClass.getObjectContext());
	}

	public boolean isHasTutorRoles() {
		return courseClass.getTutorRoles().size() > 0;
	}

	public String getTutorName() {
		Contact contact = tutorRole.getTutor().getContact();
		if (Boolean.TRUE.equals(contact.getIsCompany())) {
			return contact.getFamilyName();
		} else {
			return String.format("%s %s", contact.getGivenName(), contact.getFamilyName());
		}
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

	public List<Session> getSortedTimelineableSessions() {		
		Collections.sort(timelineableSessions, new Comparator<Session>() {
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
		return timelineableSessions;
	}

	public String getClassSessions() {
		int numberOfSession = courseClass.getSessions().size();
		int numberOfDay = sessionDays.size();
		String key = isHasSessionsInTheSameDay() ?
			(numberOfDay > 1) ? "%s days, %s hours total" : "%s day, %s hours total" :
				(numberOfSession > 1) ? "%s sessions, %s hours total" : "%s session, %s hours total";
		return String.format(key, isHasSessionsInTheSameDay() ? numberOfDay : numberOfSession,
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
		return index == visibleTutorRoles.size() - 1;
	}

	public boolean isCurrentClass() {
		return courseClass.getIsWebVisible() && courseClass.getIsActive() && !courseClass.isCancelled() &&
				(!courseClass.hasEnded() || courseClass.getIsDistantLearningCourse());
	}

	public boolean isFinishedClass() {
		return !courseClass.isCancelled() && courseClass.hasEnded();
	}

	public boolean isHasAvailableEnrolmentPlaces() {
		return courseClass.isHasAvailableEnrolmentPlaces() && (context != null || new CheckClassAge().courseClass(courseClass).classAge(preferenceController.getStopWebEnrolmentsAge()).check());
	}

	public boolean isPaymentGatewayEnabled() {
		return courseClass != null && preferenceController.isPaymentGatewayEnabled();
	}

	public boolean isHasManySessions() {
		return sessionDays.size() > 1 && courseClass != null && courseClass.isHasManySessions() && courseClass.isSessionsHaveDifferentTimes();
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

	/**
	 * The method returns value "classCommenced" for tag's attribute "class" if courseClass.firstSession start date is null
	 * or less the today
	 */
	public String getClassCommenced()
	{
		Session session = courseClass.getFirstSession();
		Date date = session != null ? session.getStartDate():null;
		return date == null || date.compareTo(new Date()) < 0 ? CLASS_NAME_classCommenced: StringUtils.EMPTY;
	}

	public Format getDateFormat() {
		return endDateFormat;
	}

	private boolean needFormatWithoutYear() {
		Date endDate = courseClass.getEndDate();
		Date startDate = courseClass.getStartDate();
		return startDate != null && endDate != null && DateUtils.truncate(endDate, Calendar.YEAR).equals(DateUtils.truncate(startDate, Calendar.YEAR));
	}
}
