package ish.oncourse.services.paymentexpress

import ish.oncourse.model.PaymentOut
import ish.oncourse.model.PaymentOutTransaction
import ish.oncourse.model.PaymentTransaction
import ish.oncourse.services.payment.PaymentRequest
import ish.oncourse.services.payment.PaymentResponse
import ish.oncourse.services.persistence.ICayenneService
import ish.oncourse.util.payment.PaymentInModel
import org.apache.cayenne.ObjectContext
import org.apache.tapestry5.ioc.annotations.Scope

@Scope("perthread")
class NewPaymentExpressGatewayService implements INewPaymentGatewayService {

	private final ICayenneService cayenneService;


	def NewPaymentExpressGatewayService(ICayenneService cayenneService) {
		this.cayenneService = cayenneService
	}

	def submit(PaymentInModel model, String billingId) {
		submit(model, null, billingId)
	}
	
	def submit(PaymentInModel model, PaymentRequest paymentRequest = null, String billingId = null) {

		ObjectContext transactionContext = cayenneService.newNonReplicatingContext()
		PaymentTransaction currentTransaction = transactionContext.newObject(PaymentTransaction.class)
		currentTransaction.setPayment(transactionContext.localObject(model.paymentIn))
		transactionContext.commitChanges()
		
		DPSRequest request = paymentRequest ? DPSRequestBuilder.valueOf(model.paymentIn, paymentRequest).build() : DPSRequestBuilder.valueOf(model.paymentIn, billingId).build()
		DPSResponse response = createGatewayHelper().submitRequest(request)
		
		ProcessDPSResponse.valueOf(model, response).process()
		ProcessPaymentTransaction.valueOf(currentTransaction, response).process()
	}

	def submit(PaymentOut paymentOut) {
		
		ObjectContext transactionContext = cayenneService.newNonReplicatingContext()
		PaymentOutTransaction currentTransaction = transactionContext.newObject(PaymentOutTransaction.class)
		currentTransaction.setCreated(new Date())
		currentTransaction.setModified(new Date())
		currentTransaction.setPaymentOut(transactionContext.localObject(paymentOut))
		
		DPSRequest request = DPSRequestBuilder.valueOf(paymentOut).build()
		DPSResponse response = createGatewayHelper().submitRequest(request)
		
		ProcessDPSResponse.valueOf(paymentOut, response).process()
		ProcessPaymentTransaction.valueOf(currentTransaction, response).process()

	}

	protected GatewayHelper createGatewayHelper() {
		return new GatewayHelper()
	}

}
