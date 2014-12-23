/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.enrol.checkout;

import ish.oncourse.model.Application;

public class ActionDisableApplication extends APurchaseAction {

	private Application application;
	
	@Override
	protected void makeAction() {
		getModel().disableApplication(application);
	}

	@Override
	protected void parse() {
		if (getParameter() != null) {
			application = getParameter().getValue(Application.class);
		}
	}

	@Override
	protected boolean validate() {
		return getModel().isApplicationEnabled(application);
	}

	public void setApplication(Application application) {
		this.application = application;
	}
}
