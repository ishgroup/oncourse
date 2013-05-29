package ish.oncourse.enrol.pages;

import ish.oncourse.enrol.checkout.PurchaseController;
import ish.oncourse.enrol.components.checkout.payment.CorporatePassEditor;
import ish.oncourse.enrol.components.checkout.payment.PaymentEditor;
import ish.oncourse.util.FormatUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Id;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
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
		/**
		 * The check was added to handle expire session for ajax requests correctly.
		 */
		if (request.isXHR())
			return null;
        if (isInitRequest())
            return Checkout.class.getSimpleName();
        else if (getPurchaseController().isEditCheckout()) {
            getPurchaseController().addError(PurchaseController.Message.illegalState);
            return Checkout.class.getSimpleName();
        } else
            return null;
    }

	private boolean isInitRequest()
	{
		return getPurchaseController() == null && request.getPath().toLowerCase().equals("/payment");
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

	public Object onException(Throwable throwable)
	{
		return checkoutPage.onException(throwable);
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
