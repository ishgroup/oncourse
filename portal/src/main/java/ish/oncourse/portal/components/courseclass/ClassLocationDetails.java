/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.components.courseclass;

import ish.oncourse.model.CourseClass;
import ish.oncourse.services.site.SiteDetails;
import ish.oncourse.services.IReachtextConverter;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * User: akoiro
 * Date: 9/08/2016
 */
public class ClassLocationDetails {
	@Inject
	private IReachtextConverter textileConverter;

	@Parameter(required = true)
	private CourseClass courseClass;

	@Property
	private SiteDetails siteDetails;


	@SetupRender
	public void setupRender() {
		if (courseClass.getRoom() != null) {
			this.siteDetails = SiteDetails.valueOf(courseClass.getRoom(), textileConverter);
		} else {
			this.siteDetails = SiteDetails.valueOf();
		}
	}
}
