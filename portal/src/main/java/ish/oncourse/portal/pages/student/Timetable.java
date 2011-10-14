package ish.oncourse.portal.pages.student;

import ish.oncourse.model.Contact;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.annotations.UserRole;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

@UserRole("student")
public class Timetable {

	@Inject
	private IAuthenticationService authService;

	@Property
	private Contact currentUser;
	
	@Property
	private String timetableMonthUrl;
	
	@Inject
	private Request request;

	@SetupRender
	void setupRender() {
		this.currentUser = authService.getUser();
		this.timetableMonthUrl = request.getContextPath() + "/timetableJson";
	}
	
	public String getMonthPageName() {
		return authService.isTutor() ? "tutor/timetable" : "student/timetable";
	}
	
	public String getListPageName() {
		return authService.isTutor() ? "tutor/timetableList" : "student/timetableList";
	}
}
