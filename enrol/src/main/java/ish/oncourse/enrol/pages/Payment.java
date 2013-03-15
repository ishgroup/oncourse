package ish.oncourse.enrol.pages;

import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.enrol.components.checkout.payment.CorporatePassEditor;
import ish.oncourse.enrol.components.checkout.payment.PaymentEditor;
import ish.oncourse.util.FormatUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import static ish.oncourse.enrol.checkout.PurchaseController.Action.selectCardEditor;
import static ish.oncourse.enrol.checkout.PurchaseController.Action.selectCorporatePassEditor;
import static ish.oncourse.enrol.checkout.PurchaseController.ActionParameter;

public class Payment {

	@InjectPage
	private Checkout checkoutPage;

	@Inject
	@Id("payment")
	private Block paymentBlock;

	@Inject
	private Request request;

    @InjectComponent
    private CorporatePassEditor corporatePassEditor;

    @InjectComponent
    private PaymentEditor paymentEditor;


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

	public Block getPaymentBlock()
	{
		return paymentBlock;
	}

    public PurchaseController getPurchaseController() {
		return checkoutPage.getPurchaseController();
	}

    public boolean isEditPayments()
    {
        return getPurchaseController() != null && (getPurchaseController().isEditPayment() ||
                getPurchaseController().isEditCorporatePass());
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

    public Object makePayment() {
        if (getPurchaseController().isEditPayment())
		{
			paymentEditor.makePayment();
			return this;
		}
        else if (getPurchaseController().isEditCorporatePass())
		{
			corporatePassEditor.makePayment();
			return this;
		}
        else
            throw new IllegalArgumentException();
    }

    public String getCorporatePassTabClass()
    {
        return getPurchaseController().isEditCorporatePass() ? "active": FormatUtils.EMPTY_STRING;
    }

    public String getCardTabClass()
    {
        return getPurchaseController().isEditPayment() ? "active": FormatUtils.EMPTY_STRING;
    }

    @OnEvent(value = "selectCardEditor")
    public Object selectCardEditor()
    {
        if (getPurchaseController().isEditPayment())
            return null;
        ActionParameter parameter = new ActionParameter(selectCardEditor);
        getPurchaseController().performAction(parameter);
        return paymentBlock;
    }

    @OnEvent(value = "selectCorporatePassEditor")
    public Object selectCorporatePassEditor()
    {
        if (getPurchaseController().isEditCorporatePass())
            return null;
        ActionParameter parameter = new ActionParameter(selectCorporatePassEditor);
        getPurchaseController().performAction(parameter);
        return paymentBlock;
    }
}
