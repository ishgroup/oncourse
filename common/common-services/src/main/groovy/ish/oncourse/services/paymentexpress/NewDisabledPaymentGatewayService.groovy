package ish.oncourse.services.paymentexpress

import ish.oncourse.model.PaymentOut
import ish.oncourse.util.payment.PaymentInModel

class NewDisabledPaymentGatewayService implements INewPaymentGatewayService {
	@Override
	def submit(PaymentInModel model, String billingId = null) {
		throw new IllegalArgumentException();
	}


	@Override
	def submit(PaymentOut paymentOut) {
		throw new IllegalArgumentException();
	}
}
