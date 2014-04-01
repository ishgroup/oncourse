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
	private Contact contact;

	@InjectPage
	private Login login;



	@SetupRender
	void setupRender() {
		this.contact = authService.getUser();
	}

	public Object onActionFromLogout() throws Exception {
		authService.logout();
		return login;
	}
}
