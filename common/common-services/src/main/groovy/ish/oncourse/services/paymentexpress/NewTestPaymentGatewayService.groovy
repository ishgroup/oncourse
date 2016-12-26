package ish.oncourse.services.paymentexpress

import groovy.transform.CompileStatic
import ish.oncourse.model.PaymentOut
import ish.oncourse.services.payment.PaymentRequest
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.util.payment.CreditCardValidator
import ish.oncourse.util.payment.PaymentInModel

@CompileStatic
class NewTestPaymentGatewayService  implements INewPaymentGatewayService {

	private final ICayenneService cayenneService

	NewTestPaymentGatewayService(ICayenneService cayenneService) {
		this.cayenneService = cayenneService
	}

	@Override
	submit(PaymentInModel model, String billingId = null) {
		def service = new TestPaymentGatewayService(cayenneService)
		billingId ? service.performGatewayOperation(model, billingId) : service.performGatewayOperation(model)
	}


	@Override
	submit(PaymentOut paymentOut) {
		new TestPaymentGatewayService(cayenneService).performGatewayOperation(paymentOut)
	}

	@Override
	submit(PaymentInModel model, PaymentRequest response) {

		model.paymentIn.creditCardCVV = response.cvv
		model.paymentIn.creditCardNumber= response.number
		model.paymentIn.creditCardName= response.name
		model.paymentIn.creditCardExpiry= response.month + '/' +  response.year
		model.paymentIn.creditCardType = CreditCardValidator.determineCreditCardType(response.number)

		def service = new TestPaymentGatewayService(cayenneService)
		service.performGatewayOperation(model)
		
	}
}

