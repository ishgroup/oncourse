package ish.oncourse.enrol.checkout;

public class ActionCancelAddPayer extends APurchaseAction {
    @Override
    protected void makeAction() {
        getController().setAddContactController(null);
        getController().setState(PurchaseController.State.editPayment);
    }

    @Override
    protected void parse() {
    }

    @Override
    protected boolean validate() {
        return true;
    }
}
