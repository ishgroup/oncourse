package ish.oncourse.services.paymentexpress

import groovy.transform.CompileStatic
import ish.oncourse.model.PaymentOut
import ish.oncourse.services.payment.PaymentRequest
import ish.oncourse.util.payment.CreditCardValidator
import ish.oncourse.util.payment.PaymentInModel
import org.apache.cayenne.ObjectContext

@CompileStatic
class NewTestPaymentGatewayService  implements INewPaymentGatewayService {

	private final ObjectContext nonReplicatingContext

	NewTestPaymentGatewayService(ObjectContext nonReplicatingContext) {
		this.nonReplicatingContext = nonReplicatingContext
	}

	@Override
	submit(PaymentInModel model, String billingId = null) {
		def service = new TestPaymentGatewayService(nonReplicatingContext)
		billingId ? service.performGatewayOperation(model, billingId) : service.performGatewayOperation(model)
	}


	@Override
	submit(PaymentOut paymentOut) {
		new TestPaymentGatewayService(nonReplicatingContext).performGatewayOperation(paymentOut)
	}

	@Override
	submit(PaymentInModel model, PaymentRequest response) {

		model.paymentIn.creditCardCVV = response.cvv
		model.paymentIn.creditCardNumber= response.number
		model.paymentIn.creditCardName= response.name
		model.paymentIn.creditCardExpiry= response.month + '/' +  response.year
		model.paymentIn.creditCardType = CreditCardValidator.determineCreditCardType(response.number)

		def service = new TestPaymentGatewayService(nonReplicatingContext)
		service.performGatewayOperation(model)
		
	}
}

