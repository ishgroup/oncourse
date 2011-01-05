package ish.oncourse.enrol.services.payment;

import ish.oncourse.model.PaymentIn;

/**
 * Payment Express gateway processing. {@inheritDoc}
 * 
 * @author ksenia
 * 
 */
public class PaymentExpressGatewayService implements IPaymentGatewayService {

	/**
	 * Performs Payment Express gateway.
	 * {@inheritDoc}
	 * @see ish.oncourse.enrol.services.payment.IPaymentGatewayService#performGatewayOperation(ish.oncourse.model.PaymentIn)
	 */
	@Override
	public void performGatewayOperation(PaymentIn payment) {
		// TODO implement logic of the willow-wo/ishframeworks/ISHPaymentExpress
	}

}
