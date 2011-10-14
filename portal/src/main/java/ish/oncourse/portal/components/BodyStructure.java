package ish.oncourse.portal.components;

import ish.oncourse.portal.access.IAuthenticationService;

import org.apache.tapestry5.ioc.annotations.Inject;

public class BodyStructure {

	@Inject
	private IAuthenticationService authenticationService;

	public boolean isUserInSession() {
		return authenticationService.getUser() != null;
	}
}
