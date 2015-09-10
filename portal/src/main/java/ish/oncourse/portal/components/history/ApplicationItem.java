/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.components.history;

import ish.common.types.ApplicationStatus;
import ish.oncourse.model.Application;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.annotations.Parameter;

import java.util.Date;

public class ApplicationItem {

	@Parameter
	private Application application;

	public boolean isAvalibleForEnrol() {
		return ApplicationStatus.OFFERED.equals(application.getStatus()) && (application.getEnrolBy() == null || application.getEnrolBy().after(new Date()));
	}

	public boolean showReason() {
		return ApplicationStatus.REJECTED.equals(application.getStatus()) && StringUtils.trimToNull(application.getReason()) != null;
	}

	public void setApplication(Application application) {
		this.application = application;
	}
	
	public Application getApplication(){
		return application;
	}
}

