package ish.oncourse.portal.components;

import ish.oncourse.model.CourseClass;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.components.subscriptions.WaitingLists;
import ish.oncourse.portal.services.discussion.IDiscussionService;
import ish.oncourse.services.courseclass.CourseClassFilter;
import ish.oncourse.services.courseclass.ICourseClassService;
import ish.oncourse.util.FormatUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.List;

public class Menu {

	@Inject
	private IAuthenticationService authenticationService;

	@Inject
	private IDiscussionService discussionService;

	@Inject
	@Property
	private Request request;

	@Inject
	private ICourseClassService courseClassService;

	@Property
	private List<CourseClass> courseClasses;

	@Property
	private CourseClass courseClass;

	@Property
	private List<CourseClass> pastCourseClasses;

	@Property
	private CourseClass nearestCourseClass;

	@SetupRender
	public void setupRender() {
		courseClasses = courseClassService.getContactCourseClasses(authenticationService.getUser(), CourseClassFilter.CURRENT);
		pastCourseClasses = courseClassService.getContactCourseClasses(authenticationService.getUser(), CourseClassFilter.PAST);
		nearestCourseClass = !courseClasses.isEmpty() ? courseClasses.get(0) : null;
		if (nearestCourseClass == null)
			nearestCourseClass = !pastCourseClasses.isEmpty() ? pastCourseClasses.get(0) : null;
	}

	public String getDate()
	{
		if (courseClass.getStartDate() != null)
			return FormatUtils.getDateFormat(FormatUtils.dateFormatString,courseClass.getTimeZone()).format(courseClass.getStartDate());
		else
			return StringUtils.EMPTY;
	}

	public boolean isHasNewMessages() {
		return getNumberOfNewMessages() > 0;
	}

	public Integer getNumberOfNewMessages() {
		return discussionService.getNumberOfNewMessages(authenticationService.getUser());
	}

	public boolean getIsTutor() {
		return authenticationService.isTutor();
	}

	public String getTimetablePageName() {
		return "timetable";
	}

	public String getDiscussionsPageName() {
		return getIsTutor() ? "tutor/messages" : "student/messages";
	}

	public String getClassesPageName() {
		return "classes";
	}

	public String getProfilePageName() {
		return "profile";
	}

	public String getMailinglistsPageName() {
		return "mailinglists";
	}

	public String getSurveysPageName() {
		return getIsTutor() ? "tutor/surveys" : "student/surveys";
	}

	public String getUnconfirmed() {
		return CourseClassFilter.UNCONFIRMED.name();
	}

	public String getCurrent() {
		return CourseClassFilter.CURRENT.name();
	}

	public String getPast() {
		return CourseClassFilter.PAST.name();
	}

	public String getWaitingListsPageName() {
		return WaitingLists.class.getSimpleName();
	}

	public String getContextPath() {
		return request.getContextPath();
	}

}
