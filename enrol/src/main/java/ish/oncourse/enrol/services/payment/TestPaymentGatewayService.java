package ish.oncourse.enrol.services.payment;

import ish.common.types.CreditCardType;
import ish.common.types.PaymentStatus;
import ish.oncourse.model.PaymentIn;



public class TestPaymentGatewayService implements IPaymentGatewayService{

	public boolean performGatewayOperation(PaymentIn payment) {
		if(payment.getCreditCardType().equals(CreditCardType.MASTERCARD)){
			payment.setStatus("Success");
			return true;
		}
		payment.setStatus("Failed");
		return false;
	}

}
