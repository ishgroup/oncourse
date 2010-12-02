package ish.oncourse.enrol.services.payment;

import ish.common.types.CreditCardType;
import ish.oncourse.model.PaymentIn;



public class TestPaymentGatewayService implements IPaymentGatewayService{

	public boolean performGatewayOperation(PaymentIn payment) {
		if(payment.getCreditCardType().equals(CreditCardType.MASTERCARD)){
			return true;
		}
		return false;
	}

}
