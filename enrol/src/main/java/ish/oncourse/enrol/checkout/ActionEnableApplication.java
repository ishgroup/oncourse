/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.enrol.checkout;

import ish.oncourse.model.Application;

import static ish.oncourse.enrol.checkout.PurchaseController.Message.applicationReceived;


public class ActionEnableApplication extends APurchaseAction {

	private Application application;
	
	@Override
	protected void makeAction() {
		getModel().enableApplication(application);
	}

	@Override
	protected void parse() {
		if (getParameter() != null) {
			application = getParameter().getValue(Application.class);
		}
	}

	@Override
	protected boolean validate() {
		return !getModel().isApplicationEnabled(application) && isAlreadyExist();
	}

	public void setApplication(Application application) {
		this.application = application;
	}
	
	private boolean isAlreadyExist() {
		Application application = getController().getApplicationService().findNewApplicationBy(this.application.getCourse(), this.application.getStudent());
		if (application != null) {
			getModel().setErrorFor(this.application, applicationReceived.getMessage(getController().getMessages()));
			return false;
		}
		return true;
	}
	
}
