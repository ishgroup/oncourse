package ish.oncourse.enrol.services.payment;

import ish.oncourse.model.PaymentIn;

public class DisabledPaymentGatewayService implements IPaymentGatewayService {

	@Override
	public boolean performGatewayOperation(PaymentIn payment) {
		// TODO may be throw some exception here?
		return false;
	}

}
