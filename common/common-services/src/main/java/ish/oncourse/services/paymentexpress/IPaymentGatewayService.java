package ish.oncourse.services.paymentexpress;

import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentOut;

/**
 * Service for payment gateway processing.
 * @author ksenia
 *
 */
public interface IPaymentGatewayService {
	
	public static final String SUCCESS_PAYMENT_IN = "Payment successful.";
	public static final String FAILED_PAYMENT_IN = "Payment failed. Card declined.";
	public static final String SUCCESS_PAYMENT_OUT = "PaymentOut successful.";
	public static final String FAILED_PAYMENT_OUT = "PaymentOut failed. Declined by paymentExpress.";
	public static final String FAILED_PAYMENT_OUT_NULL_RESPONSE ="PaymentOut failed with null transaction response.";
	/**
	 * Performs the gateway processing on the given payment, 
	 * depending on processing results, sets the appropriate statuses to the payment-related entities.
	 * @param payment the given payment for processing.
	 */
	void performGatewayOperation(PaymentIn payment);
	
	void performGatewayOperation(PaymentOut paymentOut);
}
