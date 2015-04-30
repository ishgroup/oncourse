package ish.oncourse.services.paymentexpress;

import ish.oncourse.model.PaymentOut;
import ish.oncourse.util.payment.PaymentInModel;

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
	 * @see ish.oncourse.services.paymentexpress.IPaymentGatewayService#performGatewayOperation(ish.oncourse.util.payment.PaymentInModel)
	 */
	@Override
	public void performGatewayOperation(PaymentInModel model) {
		throw new IllegalArgumentException();
	}

	@Override
	public void performGatewayOperation(PaymentOut paymentOut) {
        throw new IllegalArgumentException();
    }
}
