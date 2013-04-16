package ish.oncourse.enrol.checkout;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentStatus;
import ish.oncourse.enrol.checkout.payment.PaymentEditorController;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.PaymentIn;

import static ish.oncourse.enrol.checkout.PurchaseController.Action.selectCardEditor;

public class ActionProceedToPayment extends APurchaseAction {
    private PaymentIn paymentIn;

    @Override
    protected void makeAction() {
		//the first time proceed
		if (getController().getPaymentEditorDelegate() == null) {
			PaymentEditorController paymentEditorController = new PaymentEditorController();
			paymentEditorController.setPurchaseController(getController());
			paymentEditorController.init();
			getController().setPaymentEditorController(paymentEditorController);
		}

		getController().refreshPrevOwingStatus();
        ActionSelectCardEditor actionSelectCardEditor = selectCardEditor.createAction(getController());
        actionSelectCardEditor.makeAction();
    }

    @Override
    protected void parse() {
        paymentIn = getParameter().getValue(PaymentIn.class);
    }

    @Override
    protected boolean validate() {

        boolean result = true;
        PaymentEditorController paymentEditorController = (PaymentEditorController) getController().getPaymentEditorDelegate();
        if (paymentEditorController != null) {
            result = !(paymentEditorController.getPaymentProcessController().isIllegalState() ||
                    paymentEditorController.getPaymentProcessController().isExpired() ||
                    paymentEditorController.getPaymentProcessController().geThrowable() != null);
        } else {
            int size = getModel().getAllEnabledEnrolments().size() + getModel().getAllEnabledProductItems().size();
            if (size < 1) {
                getController().addError(PurchaseController.Message.noEnabledItemForPurchase);
                result = false;
            }
        }

        if (paymentEditorController == null && result)
        {
            getModel().deleteDisabledItems();
            prepareToMakePayment();
            result = getController().validateEnrolments() && getController().validateProductItems();
        }
        return result;
    }


    public void prepareToMakePayment() {

        getController().updateTotalIncGst();

        getModel().getPayment().setStatus(PaymentStatus.IN_TRANSACTION);

        for (Contact contact : getModel().getContacts()) {
            for (Enrolment e : getModel().getEnabledEnrolments(contact)) {
                e.setStatus(EnrolmentStatus.IN_TRANSACTION);
            }
        }
    }


}
