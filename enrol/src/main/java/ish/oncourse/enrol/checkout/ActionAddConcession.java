package ish.oncourse.enrol.checkout;

import ish.oncourse.model.StudentConcession;

public class ActionAddConcession extends APurchaseAction {
	private StudentConcession studentConcession;

	@Override
	protected void makeAction() {
		getModel().addConcession(studentConcession);
		getController().recalculateEnrolmentInvoiceLines();
		getController().setState(PurchaseController.State.editCheckout);
	}

	@Override
	protected void parse() {
		if (getParameter() != null) {
			studentConcession = getParameter().getValue(StudentConcession.class);
		}
	}

	@Override
	protected boolean validate() {
		return true;
	}
}
