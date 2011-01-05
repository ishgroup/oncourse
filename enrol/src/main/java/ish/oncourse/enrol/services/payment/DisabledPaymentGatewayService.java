package ish.oncourse.enrol.services.payment;

import ish.oncourse.model.PaymentIn;

/**
 * Stub that indicated that payment gateway processing is disabled. Normally,
 * shouldn't be used.
 * 
 * @author ksenia
 * 
 */
public class DisabledPaymentGatewayService implements IPaymentGatewayService {

	/**
	 * Do nothing if the gateway operation is invoked within payment gateway
	 * disabled college. {@inheritDoc}
	 * 
	 * @see ish.oncourse.enrol.services.payment.IPaymentGatewayService#performGatewayOperation(ish.oncourse.model.PaymentIn)
	 */
	@Override
	public void performGatewayOperation(PaymentIn payment) {
		// TODO may be throw some exception here?
	}

}
