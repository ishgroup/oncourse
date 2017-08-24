package ish.oncourse.services.paymentexpress;

import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentOut;
import ish.oncourse.util.payment.PaymentInModel;

import javax.xml.rpc.ServiceException;

/**
 * Service for payment gateway processing.
 * @author ksenia
 *
 */
public interface IPaymentGatewayService {
	
	public static final String SUCCESS_PAYMENT_IN = "Payment successful.";
	public static final String FAILED_PAYMENT_IN = "Payment failed. Card declined.";
	public static final String UNKNOW_RESULT_PAYMENT_IN = "PaymentExpress response is not valid";
	public static final String SUCCESS_PAYMENT_OUT = "PaymentOut successful.";
	public static final String FAILED_PAYMENT_OUT = "PaymentOut failed. Declined by paymentExpress.";
	public static final String FAILED_PAYMENT_OUT_NULL_RESPONSE ="PaymentOut failed with null transaction response.";
	/**
	 * Performs the gateway processing on the given paymentModel, 
	 * depending on processing results, sets the appropriate statuses to the payment-related entities.
	 * @param model the given paymentModel for processing.
	 * @deprecated @see {@link INewPaymentGatewayService#}
	 */
	@Deprecated
	void performGatewayOperation(PaymentInModel model);

	void performGatewayOperation(PaymentInModel model, String billingId);
	
	void performGatewayOperation(PaymentOut paymentOut);

	TransactionResult checkPaymentTransaction(PaymentIn p) throws ServiceException;
}
