package ish.oncourse.webservices.components;

import ish.oncourse.webservices.pages.Payment;
import ish.oncourse.webservices.utils.PaymentProcessController;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import static ish.oncourse.webservices.utils.PaymentProcessController.PaymentAction.*;

public class PaymentResult {


    private PaymentProcessController.PaymentAction paymentAction;

	@Inject
	private ComponentResources componentResources;

	@Inject
	private Request request;

    @InjectPage
    private Payment paymentPage;


    public boolean isError() {
        return paymentPage.getPaymentProcessController().getCurrentState().equals(PaymentProcessController.PaymentProcessState.ERROR);
    }

    public boolean isPaymentFailed() {
        return paymentPage.getPaymentProcessController().getCurrentState().equals(PaymentProcessController.PaymentProcessState.FAILED);
 	}
	
	public boolean isNotProcessed() {
        return paymentPage.getPaymentProcessController().getCurrentState().equals(PaymentProcessController.PaymentProcessState.NOT_PROCESSED);
    }
	
	public boolean isPaymentSuccess() {
        return paymentPage.getPaymentProcessController().getCurrentState().equals(PaymentProcessController.PaymentProcessState.SUCCESS);
    }

   public boolean isPaymentStatusNodeNullTransactionResponse() {
		return "Null transaction response".equals(paymentPage.getPaymentProcessController().getPaymentIn().getStatusNotes());
	}

	@OnEvent(component = "tryOtherCard", value = "selected")
	void submitTryOtherCard() {
        paymentAction = TRY_ANOTHER_CARD;
	}

	@OnEvent(component = "abandonReverse", value = "selected")
	void submitAbandonAndReverse() {
        paymentAction = ABANDON_PAYMENT;
    }

    @OnEvent(component = "abandonAndKeep", value = "selected")
    void  submitAbandonAndKeep()
    {
        paymentAction = ABANDON_PAYMENT_KEEP_INVOICE;
    }

	@OnEvent(component = "paymentResultForm", value = "success")
	Object submitted() {
		Payment paymentPage = (Payment) componentResources.getPage();
        switch (paymentAction)
        {
            case TRY_ANOTHER_CARD:
                paymentPage.getPaymentProcessController().processAction(TRY_ANOTHER_CARD);
                break;
            case ABANDON_PAYMENT:
                paymentPage.getPaymentProcessController().processAction(ABANDON_PAYMENT);
                break;
            case ABANDON_PAYMENT_KEEP_INVOICE:
                this.paymentPage.getPaymentProcessController().processAction(ABANDON_PAYMENT_KEEP_INVOICE);
                break;
            default:
                throw new IllegalArgumentException();
        }
        return this.paymentPage.getPageURL();
	}
}
