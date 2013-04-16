package ish.oncourse.enrol.checkout;

import ish.common.types.EnrolmentStatus;
import ish.oncourse.model.Enrolment;

import java.util.List;

import static ish.oncourse.enrol.checkout.PurchaseController.Message.corporatePassShouldBeEntered;

public class ActionMakePayment extends APurchaseAction {
	@Override
	protected void makeAction() {
		if (getController().isEditCorporatePass()) {
			getController().setPaymentEditorController(null);
			getModel().deletePayment();
			getController().setState(PurchaseController.State.paymentResult);
			List<Enrolment> enrolments = getController().getModel().getAllEnabledEnrolments();
			for (Enrolment enrolment : enrolments) {
				enrolment.setStatus(EnrolmentStatus.SUCCESS);
			}
		} else if (getController().isEditPayment()) {
			getController().setState(PurchaseController.State.paymentProgress);
		} else
			throw new IllegalArgumentException();
		getModel().getObjectContext().commitChanges();
	}

	@Override
	protected void parse() {
	}

	@Override
	protected boolean validate() {
		if (getController().isEditCorporatePass()) {
			if (getController().getModel().getCorporatePass() == null) {
				getController().addError(corporatePassShouldBeEntered);
				return false;
			} else
				return getController().validateEnrolments();
		} else if (getController().isEditPayment()) {
			return getController().validateEnrolments() && getController().validateProductItems();
		} else
			throw new IllegalArgumentException();
	}
}
