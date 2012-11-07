package ish.oncourse.enrol.checkout;

import ish.oncourse.enrol.checkout.payment.PaymentEditorController;

public class ActionShowPaymentResult extends APurchaseAction {
	@Override
	protected void makeAction() {
		getController().setState(PurchaseController.State.finalized);
	}

	@Override
	protected void parse() {
	}

	@Override
	protected boolean validate() {
		PaymentEditorController paymentEditorController = (PaymentEditorController) getController().getPaymentEditorDelegate();
		return paymentEditorController.getPaymentProcessController().isFinalState();
	}
}
