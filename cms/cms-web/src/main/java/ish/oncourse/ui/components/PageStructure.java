package ish.oncourse.ui.components;

import ish.oncourse.cms.services.access.IAuthenticationService;

import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * A page wrapper component.
 */
@SupportsInformalParameters
public class PageWrapper extends GenericPageStructure {

	@Inject
	private IAuthenticationService authenticationService;

	public boolean isLoggedIn() {
		return authenticationService.getUser() != null;
	}
}
