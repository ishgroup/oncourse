package ish.oncourse.enrol.checkout;

public class ActionAddPersonPayer extends AAddContactAction
{

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
        return PurchaseController.Action.addPersonPayer;
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
}
