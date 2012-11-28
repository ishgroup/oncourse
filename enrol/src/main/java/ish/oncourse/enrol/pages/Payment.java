package ish.oncourse.enrol.pages;

import ish.oncourse.enrol.checkout.PurchaseController;
import org.apache.tapestry5.annotations.InjectPage;

public class Payment {

	@InjectPage
	private Checkout checkoutPage;

	String onActivate() {
		if (getPurchaseController() == null)
			return Checkout.class.getSimpleName();
		else if (getPurchaseController().isEditCheckout()) {
			getPurchaseController().addError(PurchaseController.Error.illegalState);
			return Checkout.class.getSimpleName();
		} else
			return null;
	}

	public PurchaseController getPurchaseController() {
		return checkoutPage.getPurchaseController();
	}

    public boolean isEditPayment()
    {
        return getPurchaseController() != null && getPurchaseController().isEditPayment();
    }

    public boolean isPaymentResult()
    {
        return getPurchaseController() != null && getPurchaseController().isPaymentResult();
    }

    public boolean isPaymentProgress()
    {
        return getPurchaseController() != null && getPurchaseController().isPaymentProgress();
    }

}
