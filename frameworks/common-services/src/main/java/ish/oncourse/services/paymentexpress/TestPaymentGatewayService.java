package ish.oncourse.services.paymentexpress;

import ish.common.types.CreditCardType;
import ish.oncourse.model.PaymentIn;
import ish.oncourse.model.PaymentOut;

/**
 * Test payment gateway processing.
 * 
 * @author ksenia
 * 
 */
public class TestPaymentGatewayService implements IPaymentGatewayService {

	/**
	 * {@inheritDoc} <br/>
	 * Success if the credit card type is {@link CreditCardType#MASTERCARD},
	 * fail otherwise.
	 * 
	 * @see ish.oncourse.services.paymentexpress.IPaymentGatewayService#performGatewayOperation(ish.oncourse.model.PaymentIn)
	 */
	public void performGatewayOperation(PaymentIn payment) {
		if (payment.getCreditCardType().equals(CreditCardType.MASTERCARD)) {
			payment.succeed();
		} else {
			payment.failed();
		}
	}

	@Override
	public void performGatewayOperation(PaymentOut paymentOut) {
		if (paymentOut.getTotalAmount().longValue() < 1000) {
			paymentOut.succeed();
		} else {
			paymentOut.failed();
		}
	}
}
