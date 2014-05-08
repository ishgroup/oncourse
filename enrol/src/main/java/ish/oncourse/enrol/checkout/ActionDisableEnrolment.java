package ish.oncourse.enrol.checkout;

import ish.common.types.EnrolmentStatus;
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
		getModel().disableEnrolment(enrolment);
        //we set status NEW for disabled enrolment  to exclude the enrolment in places check
        enrolment.setStatus(EnrolmentStatus.NEW);
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
