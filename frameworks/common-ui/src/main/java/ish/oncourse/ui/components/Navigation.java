package ish.oncourse.ui.components;

import ish.oncourse.services.node.IWebNodeService;
import ish.oncourse.services.security.IAuthenticationService;
import ish.oncourse.services.security.Protected;

import java.io.IOException;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

@Protected
public class Navigation {

	@Property
	@Inject
	private IWebNodeService webNodeService;

	@Inject
	private IAuthenticationService authenticationService;

	public boolean isHasCurrentNode() {
		return webNodeService.getCurrentNode() != null;
	}

	public Object onActionFromLogout() throws IOException {
		authenticationService.logout();
		return null;
	}
}
