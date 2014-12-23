/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.enrol.checkout;

import ish.common.types.ConfirmationStatus;
import ish.oncourse.enrol.checkout.payment.PaymentEditorController;

public class ActionConfirmApplication extends APurchaseAction {
	@Override
	protected void makeAction() {
		getModel().deletePayment();
		getModel().deleteInvoice();

		PaymentEditorController paymentEditorController = new PaymentEditorController();
		paymentEditorController.setPurchaseController(getController());
		getController().setPaymentEditorController(paymentEditorController);
		getController().setState(PurchaseController.State.paymentResult);

		getController().setConfirmationStatus(ConfirmationStatus.NOT_SENT);

		getModel().getObjectContext().commitChanges();
	}

	@Override
	protected void parse() {

	}

	@Override
	protected boolean validate() {
		boolean result = getModel().isApplicationsOnly();
		if (result) {
			
			if (getModel().getAllEnabledApplications().size() < 1) {
				getController().addError(PurchaseController.Message.noEnabledItemForPurchase);
				result = false;
			} else {
				getModel().deleteDisabledItems();
			}
			return result;
		}
		
		throw new IllegalStateException(String.format("Can not confirm applications for state: %s and  model which contains %s invoiceLines and %s enabled applications", 
				getController().getState(), getModel().getInvoice().getInvoiceLines().size(), getModel().getAllEnabledApplications().size()));
	}
}
