package ish.oncourse.portal.pages;

import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.annotations.UserRole;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.ioc.annotations.Inject;

@UserRole({"student", "tutor"})
public class Index {

	@InjectPage("student/timetable")
	private Object timetableStudent;

	@InjectPage("tutor/timetable")
	private Object timetableTutor;

	@Inject
	private IAuthenticationService authenticationService;

	Object onActivate() {
		return authenticationService.isTutor() ? timetableTutor : timetableStudent;
	}
}
