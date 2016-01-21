/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.components.history;

import ish.common.types.ApplicationStatus;
import ish.math.Money;
import ish.oncourse.model.Application;
import ish.oncourse.util.FormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

import java.text.Format;
import java.util.Date;

public class ApplicationItem {

	@Parameter
	private Application application;

	@Property
	private Format moneyFormat;

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

	public Money getFeeOverride(){
		Money feeOverride = application.getFeeOverride();
		if (feeOverride == null)
			return null;
		moneyFormat = FormatUtils.chooseMoneyFormat(feeOverride);
		return feeOverride;
	}
}

