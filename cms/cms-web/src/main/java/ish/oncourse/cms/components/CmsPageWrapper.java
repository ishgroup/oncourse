package ish.oncourse.cms.components;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Cookies;

import ish.oncourse.cms.pages.Login;
import ish.oncourse.cms.services.security.IAuthenticationService;
import ish.oncourse.ui.components.PageWrapper;

public class CmsPageWrapper extends PageWrapper {
	@Inject
	private IAuthenticationService authenticationService;

	@Inject
	private Cookies cookies;

	public String getBodyClass() {
		return (authenticationService.getUser() != null) ? "cms_loggedin"
				: super.getBodyClass();
	}

	public boolean isLoggedIn() {
		boolean isLoggedIn = authenticationService.getUser() != null;
		if (isLoggedIn) {
			cookies.writeCookieValue(Login.CMS_COOKIE_NAME,
					Login.CMS_COOKIE_NAME, "/");
		}
		return isLoggedIn;
	}
}
