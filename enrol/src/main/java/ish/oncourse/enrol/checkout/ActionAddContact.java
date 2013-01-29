package ish.oncourse.enrol.checkout;

public class ActionAddContact extends AAddContactAction {

    @Override
    protected boolean shouldChangePayer() {
        return getModel().getPayer() == null;
    }

    @Override
    protected boolean shouldAddEnrolments() {
        return true;
    }

    @Override
    protected boolean isApplyOwing() {
        return false;
    }

    @Override
    protected PurchaseController.State getFinalState() {
        return PurchaseController.State.editCheckout;
    }

    @Override
    protected PurchaseController.Action getAddAction() {
        return PurchaseController.Action.addContact;
    }

    @Override
    protected PurchaseController.Action getCancelAction() {
        return PurchaseController.Action.cancelAddContact;
    }
}
