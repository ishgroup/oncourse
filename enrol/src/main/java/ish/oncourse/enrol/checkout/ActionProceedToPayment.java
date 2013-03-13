package ish.oncourse.enrol.checkout;

import ish.common.types.EnrolmentStatus;
import ish.common.types.PaymentStatus;
import ish.oncourse.enrol.checkout.payment.PaymentEditorController;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Enrolment;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.ProductItem;
import ish.oncourse.util.payment.PaymentProcessController;

import java.util.List;

import static ish.oncourse.enrol.checkout.PurchaseController.Action.*;

public class ActionProceedToPayment extends APurchaseAction {
    private PaymentIn paymentIn;

    @Override
    protected void makeAction() {
        //the first time proceed
        if (getController().getPaymentEditorDelegate() == null) {
            PaymentProcessController paymentProcessController = new PaymentProcessController();
            paymentProcessController.setStartWatcher(false);
            paymentProcessController.setObjectContext(getModel().getObjectContext());
            paymentProcessController.setPaymentIn(getModel().getPayment());
            paymentProcessController.setCayenneService(getController().getCayenneService());
            paymentProcessController.setPaymentGatewayService(getController().getPaymentGatewayServiceBuilder().buildService());
            paymentProcessController.setParallelExecutor(getController().getParallelExecutor());
            paymentProcessController.processAction(PaymentProcessController.PaymentAction.INIT_PAYMENT);
            PaymentEditorController paymentEditorController = new PaymentEditorController();
            paymentEditorController.setPaymentProcessController(paymentProcessController);
            paymentEditorController.setPurchaseController(getController());
            getController().setPaymentEditorController(paymentEditorController);
        } else {
            getModel().setPayment(paymentIn);
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
            getModel().getObjectContext().commitChanges();
            result = validateEnrolments() && validateProductItems();
        }
        return result;
    }

    private boolean validateEnrolments() {
        ActionEnableEnrolment actionEnableEnrolment = enableEnrolment.createAction(getController());
        List<Enrolment> enrolments = getController().getModel().getAllEnabledEnrolments();
        boolean result = true;
        for (Enrolment enrolment : enrolments) {
            actionEnableEnrolment.setEnrolment(enrolment);
            boolean valid = actionEnableEnrolment.validateEnrolment();
            if (!valid) {
                ActionDisableEnrolment actionDisableEnrolment = disableEnrolment.createAction(getController());
                actionDisableEnrolment.setEnrolment(enrolment);
                actionDisableEnrolment.action();
                getModel().removeEnrolment(enrolment);
            }
            result = result && valid;
        }
        return result;
    }

    private boolean validateProductItems() {
        ActionEnableProductItem actionEnableProductItem = PurchaseController.Action.enableProductItem.createAction(getController());
        List<ProductItem> items = getController().getModel().getAllProductItems(getModel().getPayer());
        boolean result = true;
        for (ProductItem item : items) {
            actionEnableProductItem.setProductItem(item);
            boolean valid = actionEnableProductItem.validateProductItem();
            if (!valid) {
                ActionDisableProductItem actionDisableProductItem = disableProductItem.createAction(getController());
                actionDisableProductItem.setProductItem(item);
                actionDisableProductItem.action();
                getModel().removeProductItem(getModel().getPayer(), item);
            }
            result = result && valid;
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
