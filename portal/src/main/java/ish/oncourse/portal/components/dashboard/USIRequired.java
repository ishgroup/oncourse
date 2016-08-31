/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.components.dashboard;

import ish.oncourse.model.Contact;
import ish.oncourse.portal.services.IPortalService;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

public class USIRequired {
	
	@Inject
	private IPortalService portalService;

	@Parameter
	@Property
	private Contact contact;

	public String getProfilePicturePath() {
		return portalService.getProfilePictureUrl(contact);
	}
}
