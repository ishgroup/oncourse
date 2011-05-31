package ish.oncourse.services.paymentexpress;

import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentOut;

/**
 * Stub that indicated that payment gateway processing is disabled. Normally,
 * shouldn't be used.
 * 
 * @author ksenia
 * 
 */
public class DisabledPaymentGatewayService implements IPaymentGatewayService {

	/**
	 * {@inheritDoc} Do nothing if the gateway operation is invoked within
	 * payment gateway disabled college.
	 * 
	 * @see ish.oncourse.services.paymentexpress.IPaymentGatewayService#performGatewayOperation(ish.oncourse.model.PaymentIn)
	 */
	@Override
	public void performGatewayOperation(PaymentIn payment) {
		// TODO may be throw some exception here?
	}

	@Override
	public void performGatewayOperation(PaymentOut paymentOut) {
		// TODO Auto-generated method stub
		
	}
}
