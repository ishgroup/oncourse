package ish.oncourse.portal.components;

import ish.oncourse.model.Contact;
import ish.oncourse.portal.pages.Login;
import ish.oncourse.portal.services.IPortalService;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class LoginUser {
	
	@Inject
	private IPortalService portalService;

	@Property
	private Contact contact;

	@InjectPage
	private Login login;



	@SetupRender
	void setupRender() {
		this.contact = portalService.getAuthenticatedUser();
	}

	public Object onActionFromLogout() throws Exception {
        portalService.logout();
		return login;
	}
}
