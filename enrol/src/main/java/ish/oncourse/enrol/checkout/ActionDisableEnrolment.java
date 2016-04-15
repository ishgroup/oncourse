package ish.oncourse.enrol.checkout;

import ish.oncourse.model.Enrolment;

public class ActionDisableEnrolment extends APurchaseAction {
	private Enrolment enrolment;

	@Override
	protected void makeAction() {
		/*
		 we need to clear voucher redemption data because during the action structure of invoiceLines was changed 
		 (invoiceLine for the enrolment is removed)
		*/
		getController().getVoucherRedemptionHelper().clear();
		if (getController().isSupportPaymentPlan() && !enrolment.getCourseClass().getPaymentPlanLines().isEmpty()) {
			getModel().removePaymentPlanInvoiceFor(enrolment);
		}

		enrolment = getModel().disableEnrolment(enrolment);
		getController().updateDiscountApplied();
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
