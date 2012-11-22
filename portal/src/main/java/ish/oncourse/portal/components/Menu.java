package ish.oncourse.portal.components;

import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.pages.WaitingLists;
import ish.oncourse.portal.services.discussion.IDiscussionService;
import ish.oncourse.services.courseclass.CourseClassFilter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class Menu {

	@Inject
	private IAuthenticationService authenticationService;

	@Inject
	private IDiscussionService discussionService;

	@Inject
	private Request request;

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
