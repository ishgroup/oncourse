package ish.oncourse.portal.components;

import ish.oncourse.portal.access.IAuthenticationService;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class BodyHeader {

    @Parameter
    @Property
    private String activeMenu;
	
	@Inject
	private IAuthenticationService authenticationService;
	
	@Inject
	private Request request;

	public boolean isUserInSession() {
		return authenticationService.getUser() != null;
	}
	
	public String getContextPath() {
		return request.getContextPath();
	}
}