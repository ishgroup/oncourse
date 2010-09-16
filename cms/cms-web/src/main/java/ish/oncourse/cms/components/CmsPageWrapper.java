package ish.oncourse.cms.components;

import org.apache.tapestry5.ioc.annotations.Inject;

import ish.oncourse.cms.services.security.IAuthenticationService;
import ish.oncourse.ui.components.PageWrapper;

public class CmsPageWrapper extends PageWrapper {
	@Inject
	private IAuthenticationService authenticationService;

	public String getBodyClass() {
		return (authenticationService.getUser() != null) ? "cms_loggedin"
				: super.getBodyClass();
	}

	public boolean isLoggedIn() {
		return authenticationService.getUser() != null;
	}
}
