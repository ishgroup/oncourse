package ish.oncourse.enrol.checkout;

import ish.oncourse.enrol.checkout.model.PurchaseModel;

/**
 * Created by akoiro on 20/04/2016.
 */
public class ResetCorporatePass {
	private PurchaseController controller;
	private PurchaseModel model;


	private ResetCorporatePass() {

	}

	public void reset() {
		if (controller.isEditCorporatePass()) {
			model.getInvoice().setCorporatePassUsed(null);
			model.getInvoice().setCustomerReference(null);
			model.setCorporatePass(null);
			model.setPayer(model.getContacts().get(0));
			controller.updateDiscountApplied();
		}
	}

	public static ResetCorporatePass valueOf(PurchaseController controller) {
		ResetCorporatePass resetCorporatePass = new ResetCorporatePass();
		resetCorporatePass.controller = controller;
		resetCorporatePass.model = controller.getModel();
		return resetCorporatePass;
	}
}
