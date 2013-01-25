package ish.oncourse.enrol.checkout;

public class ActionAddPayer extends AAddContactAction
{

    @Override
    protected boolean shouldChangePayer() {
        return true;
    }

    @Override
    protected boolean shouldEnableEnrolments() {
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
        return PurchaseController.Action.addPayer;
    }

    @Override
    protected PurchaseController.Action getCancelAction() {
        return PurchaseController.Action.cancelAddPayer;
    }
}
