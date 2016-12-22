package ish.oncourse.services.paymentexpress

import ish.oncourse.model.PaymentOut
import ish.oncourse.services.payment.PaymentRequest
import ish.oncourse.services.payment.PaymentResponse
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.services.search.CourseClassUtils
import ish.oncourse.util.payment.CreditCardValidator
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

	@Override
	def submit(PaymentInModel model, PaymentRequest response) {

		model.paymentIn.creditCardCVV = response.cvv
		model.paymentIn.creditCardNumber= response.number
		model.paymentIn.creditCardName= response.name
		model.paymentIn.creditCardExpiry= response.month + '/' +  response.year
		model.paymentIn.creditCardType = CreditCardValidator.determineCreditCardType(response.number)

		def service = new TestPaymentGatewayService(cayenneService)
		service.performGatewayOperation(model)
		
	}
}

