/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.components;

import ish.common.types.ApplicationStatus;
import ish.oncourse.model.Application;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.Messages;

import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.Date;


public class ApplicationStatusComponent {

	public static final String EXPIRED_APPLICATION_MESSAGE_KEY = "expired-application-message";
	
	@Inject
	private Messages messages;
	
	@Parameter
	private Application application;
	
	public String getDisplayedLable() {
		if (ApplicationStatus.OFFERED.equals(application.getStatus()) && application.getEnrolBy() != null && application.getEnrolBy().before(new Date())) {
			return messages.get(EXPIRED_APPLICATION_MESSAGE_KEY);
		} else {
			return messages.get(application.getStatus().getDisplayName());
		}
	}
}
