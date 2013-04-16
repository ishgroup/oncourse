package ish.oncourse.enrol.checkout;

/**
 * User: akoyro
 */
public class ActionShowPaymentResult extends APurchaseAction{

    @Override
    protected void makeAction() {
        getController().setState(PurchaseController.State.paymentResult);
    }

    @Override
    protected void parse() {
    }

    @Override
    protected boolean validate() {
        return getController().getPaymentEditorDelegate().isFinalState();
    }
}
