package ish.oncourse.enrol.checkout;

import ish.oncourse.model.Enrolment;

public class ActionDisableEnrolment extends APurchaseAction {
	private Enrolment enrolment;

	@Override
	protected void makeAction() {
		getModel().disableEnrolment(enrolment);
	}

	@Override
	protected void parse() {

		if (getParameter() != null)
			enrolment = getParameter().getValue(Enrolment.class);
	}

	@Override
	protected boolean validate() {
		return getModel().isEnrolmentEnabled(enrolment);
	}

	public Enrolment getEnrolment() {
		return enrolment;
	}

	public void setEnrolment(Enrolment enrolment) {
		this.enrolment = enrolment;
	}
}
