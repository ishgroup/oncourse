package ish.oncourse.enrol.services.payment;

import ish.oncourse.model.PaymentIn;

/**
 * Service for payment gateway processing.
 * @author ksenia
 *
 */
public interface IPaymentGatewayService {
	/**
	 * Performs the gateway processing on the given payment, 
	 * depending on processing results, sets the appropriate statuses to the payment-related entities.
	 * @param payment the given payment for processing.
	 */
	void performGatewayOperation(PaymentIn payment);
}
