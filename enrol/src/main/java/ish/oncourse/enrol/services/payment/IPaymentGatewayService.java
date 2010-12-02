package ish.oncourse.enrol.services.payment;

import ish.oncourse.model.PaymentIn;

public interface IPaymentGatewayService {
	boolean performGatewayOperation(PaymentIn payment);
}
