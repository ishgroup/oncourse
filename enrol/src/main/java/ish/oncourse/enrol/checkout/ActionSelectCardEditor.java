package ish.oncourse.enrol.checkout;

import ish.common.types.PaymentType;

import static ish.oncourse.enrol.checkout.PurchaseController.State.editPayment;

public class ActionSelectCardEditor extends APurchaseAction{
    @Override
    protected void makeAction() {
		getModel().getPayment().setType(PaymentType.CREDIT_CARD);

		if (getController().isEditCorporatePass())
		{
			getModel().getInvoice().setCorporatePassUsed(null);
			getModel().getInvoice().setCustomerReference(null);
			getModel().setCorporatePass(null);
			getModel().setPayer(getModel().getContacts().get(0));
		}

		ActionChangePayer actionChangePayer = new ActionChangePayer();
		actionChangePayer.setController(getController());
		actionChangePayer.setContact(getModel().getPayer());
		actionChangePayer.action();
		getController().setState(editPayment);
    }

    @Override
    protected void parse() {
    }

    @Override
    protected boolean validate() {
        return !getController().isEditPayment();
    }
}
