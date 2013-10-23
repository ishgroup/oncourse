package ish.oncourse.portal.components;

import ish.oncourse.model.Contact;
import ish.oncourse.portal.access.IAuthenticationService;
import ish.oncourse.portal.pages.Login;
import ish.oncourse.services.courseclass.CourseClassFilter;
import ish.oncourse.services.courseclass.ICourseClassService;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class LoginUser {
	
	@Inject
	private IAuthenticationService authService;
	
	@Property
	private Contact user;
	
	@InjectPage
	private Login login;

	@Inject
	private ICourseClassService courseClassService;

	@Property
	private int approvalsCount = 0;
	
	@SetupRender
	void setupRender() {
		this.user = authService.getUser();
		approvalsCount = courseClassService.getContactCourseClasses(this.user, CourseClassFilter.UNCONFIRMED).size();
	}
	
	public Object onActionFromLogout() throws Exception {
		authService.logout();
		return login;
	}
}
