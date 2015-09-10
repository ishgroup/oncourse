/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.components;

import ish.common.types.ApplicationStatus;
import ish.oncourse.model.Application;
import ish.oncourse.model.Student;
import ish.oncourse.portal.services.IPortalService;
import ish.oncourse.portal.services.application.IPortalApplicationService;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import java.util.LinkedList;
import java.util.List;


public class ApplicationsMenu {
	
	@Inject
	@Property
	private Request request;

	@Property
	private List<Application> applications = new LinkedList<>();

	@Property
	private Application application;

	@Inject
	private IPortalService portalService;

	@Inject
	private IPortalApplicationService applicationService;


	@SetupRender
	public void setupRender() {
		Student student = portalService.getContact().getStudent();
		if (student != null) {
			applications = applicationService.getActiveApplicationsBy(student);
		}
		
	}

	public boolean needApprove() {
		return ApplicationStatus.OFFERED.equals(application.getStatus());
	}

}
