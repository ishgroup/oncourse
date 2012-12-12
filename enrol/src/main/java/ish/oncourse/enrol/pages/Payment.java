package ish.oncourse.enrol.pages;

import ish.oncourse.enrol.checkout.PurchaseController;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

public class Payment {

	@InjectPage
	private Checkout checkoutPage;

	@Inject
	private Request request;

    Object onActivate()
    {
        if (checkoutPage.isExpired())
            return null;
        if (getPurchaseController() == null)
            return Checkout.class.getSimpleName();
        else if (getPurchaseController().isEditCheckout()) {
            getPurchaseController().addError(PurchaseController.Message.illegalState);
            return Checkout.class.getSimpleName();
        } else
            return null;
    }

    @SetupRender
	void setupRender() {
    }

    @AfterRender
    void afterRender() {
        if (checkoutPage.isExpired())
            checkoutPage.resetPersistProperties();
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

	public String getCoursesLink() {
		return checkoutPage.getCoursesLink();
	}

	public boolean isExpired()
	{
		return checkoutPage.isExpired();
	}

	public void onException(Throwable throwable)
	{
		checkoutPage.onException(throwable);
	}

}
