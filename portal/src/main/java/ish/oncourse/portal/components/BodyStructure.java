package ish.oncourse.portal.components;

import ish.oncourse.portal.access.IAuthenticationService;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

public class BodyStructure {

    @Parameter
    @Property
    private String activeMenu;

	@Inject
	private IAuthenticationService authenticationService;

	public boolean isUserInSession() {
		return authenticationService.getUser() != null;
	}


}
