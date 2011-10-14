package ish.oncourse.portal.components;

import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.services.discussion.IDiscussionService;

import org.apache.tapestry5.ioc.annotations.Inject;

public class Menu {

	@Inject
	private IAuthenticationService authenticationService;

	@Inject
	private IDiscussionService discussionService;

	public boolean isHasNewMessages() {
		return getNumberOfNewMessages() > 0;
	}

	public Integer getNumberOfNewMessages() {
		return discussionService.getNumberOfNewMessages(authenticationService.getUser());
	}

	private boolean isTutor() {
		return authenticationService.isTutor();
	}

	public String getTimetablePageName() {
		return isTutor() ? "tutor/timetable" : "student/timetable";
	}

	public String getDiscussionsPageName() {
		return isTutor() ? "tutor/messages" : "student/messages";
	}

	public String getClassesPageName() {
		return isTutor() ? "tutor/classes" : "student/classes";
	}

	public String getProfilePageName() {
		return isTutor() ? "tutor/profile" : "student/profile";
	}

	public String getSurveysPageName() {
		return isTutor() ? "tutor/surveys" : "student/surveys";
	}
}
