package ish.oncourse.services.paymentexpress

import groovy.transform.CompileStatic
import ish.oncourse.model.PaymentOut
import ish.oncourse.services.payment.PaymentRequest
import ish.oncourse.util.payment.PaymentInModel

@CompileStatic
class NewDisabledPaymentGatewayService implements INewPaymentGatewayService {
	@Override
	submit(PaymentInModel model, String billingId = null) {
		throw new IllegalArgumentException()
	}


	@Override
	submit(PaymentOut paymentOut) {
		throw new IllegalArgumentException()
	}

	@Override
	submit(PaymentInModel model, PaymentRequest response) {
		throw new IllegalArgumentException()
	}
}
