/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.enrol.checkout;

import ish.oncourse.enrol.checkout.contact.AddContactController;

public class ActionAddCompanyPayer extends AAddContactAction {
	@Override
	protected boolean shouldChangePayer() {
		return true;
	}

	@Override
	protected boolean shouldAddEnrolments() {
		return false;
	}

	@Override
	protected boolean isApplyOwing() {
		return true;
	}

	@Override
	protected PurchaseController.State getFinalState() {
		return PurchaseController.State.editPayment;
	}

	@Override
	protected PurchaseController.Action getAddAction() {
		return PurchaseController.Action.addCompanyPayer;
	}

	@Override
	protected PurchaseController.Action getCancelAction() {
		return PurchaseController.Action.cancelAddPayer;
	}

	@Override
	protected String getHeaderMessage() {
		return getController().getMessages().format("message-enterDetailsForPayer");
	}

	@Override
	protected String getHeaderTitle() {
		return getController().getMessages().format("message-addPayer");
	}
	
	@Override
	protected  void initAddContact() {
		super.initAddContact();
		((AddContactController) getController().getAddContactDelegate()).setCompanyPayer(true);
	}


	@Override
	protected ContactCredentialsEncoder buildContactCredentialsEncoder() {
		ContactCredentialsEncoder result = super.buildContactCredentialsEncoder();
		result.setCompany(true);
		return result;
	}
}
