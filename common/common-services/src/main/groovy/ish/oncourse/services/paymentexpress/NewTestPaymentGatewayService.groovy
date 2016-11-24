package ish.oncourse.services.paymentexpress

import ish.oncourse.model.PaymentOut
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.util.payment.PaymentInModel

class NewTestPaymentGatewayService  implements INewPaymentGatewayService {

	private final ICayenneService cayenneService;

	def NewTestPaymentGatewayService(ICayenneService cayenneService) {
		this.cayenneService = cayenneService
	}

	@Override
	def submit(PaymentInModel model, String billingId = null) {
		def service = new TestPaymentGatewayService(cayenneService)
		billingId ? service.performGatewayOperation(model, billingId) : service.performGatewayOperation(model)
	}


	@Override
	def submit(PaymentOut paymentOut) {
		new TestPaymentGatewayService(cayenneService).performGatewayOperation(paymentOut)
	}
}

