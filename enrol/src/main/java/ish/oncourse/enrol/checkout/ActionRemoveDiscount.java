package ish.oncourse.enrol.checkout;

/**
 */
public class ActionRemoveDiscount extends ADiscountAction {

    @Override
    protected void makeAction() {
        getModel().removeDiscount(getModel().localizeObject(discount));
        getController().recalculateEnrolmentInvoiceLines();
        getModel().updateTotalDiscountAmountIncTax();
    }

}