package ish.oncourse.enrol.checkout;

import ish.oncourse.model.Enrolment;
import ish.oncourse.model.InvoiceLine;

public class ActionEnableEnrolment extends APurchaseAction{

	private Enrolment enrolment;

	@Override
	protected void makeAction() {
		getModel().enableEnrolment(enrolment);
		InvoiceLine il = getController().getInvoiceProcessingService().createInvoiceLineForEnrolment(enrolment, getModel().getDiscounts());
		il.setInvoice(getModel().getInvoice());
		enrolment.setInvoiceLine(il);
	}

	@Override
	protected void parse() {
		if (getParameter() != null)
			enrolment = getParameter().getValue(Enrolment.class);
	}

	@Override
	protected boolean validate() {
		/**
		 * TODO add this check when we try to enable enrolment
		 * if (!enrolment.isDuplicated() && courseClass.isHasAvailableEnrolmentPlaces() && !courseClass.hasEnded()) {
		 */
		return  !getModel().isEnrolmentEnabled(enrolment);
	}

	public Enrolment getEnrolment() {
		return enrolment;
	}

	public void setEnrolment(Enrolment enrolment) {
		this.enrolment = enrolment;
	}
}
