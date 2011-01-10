package ish.oncourse.enrol.services.payment;

import ish.common.types.CreditCardType;
import ish.oncourse.model.PaymentIn;

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
	 * @see ish.oncourse.enrol.services.payment.IPaymentGatewayService#performGatewayOperation(ish.oncourse.model.PaymentIn)
	 */
	public void performGatewayOperation(PaymentIn payment) {
		if (payment.getCreditCardType().equals(CreditCardType.MASTERCARD)) {
			payment.succeed();
		} else {
			payment.failed();
		}
	}

}
