package ish.oncourse.portal.components;

import ish.oncourse.portal.services.IPortalService;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

public class BodyStructure {

    @Parameter
    @Property
    private String activeMenu;

	@Inject
	private IPortalService portalService;

	public boolean isUserInSession() {
		return portalService.getAuthenticatedUser() != null;
	}


}
