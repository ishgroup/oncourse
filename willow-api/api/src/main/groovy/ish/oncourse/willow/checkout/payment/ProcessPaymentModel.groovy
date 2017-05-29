package ish.oncourse.willow.checkout.payment

import ish.common.types.PaymentType
import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.services.paymentexpress.NewPaymentExpressGatewayService
import ish.oncourse.util.payment.PaymentInSucceed
import ish.oncourse.willow.checkout.payment.execution.StartDPSExecution
import ish.oncourse.willow.model.checkout.payment.PaymentRequest
import ish.oncourse.willow.model.checkout.payment.PaymentResponse
import ish.oncourse.willow.model.checkout.payment.PaymentStatus
import ish.oncourse.willow.model.common.CommonError
import org.apache.cayenne.ObjectContext

class ProcessPaymentModel {
    
    ObjectContext context
    College college
    CreatePaymentModel createPaymentModel
    PaymentRequest paymentRequest
    
    
    CommonError error
    PaymentResponse response

    ProcessPaymentModel(ObjectContext context, College college, CreatePaymentModel createPaymentModel, PaymentRequest paymentRequest) {
        this.context = context
        this.college = college
        this.createPaymentModel = createPaymentModel
        this.paymentRequest = paymentRequest
    }

    ProcessPaymentModel process() {
        if (createPaymentModel.applicationsOnly) {
            context.commitChanges()

            response =  new PaymentResponse().with { r ->
                r.paymentStatus = PaymentStatus.SUCCESSFUL
                r.applicationIds = createPaymentModel.applications.collect { it.id.toString() }
                r
            }
            this
        } else {
            Money actualAmount = createPaymentModel.paymentIn.amount
            
            if (actualAmount != new Money(paymentRequest.payNow)) {
                context.rollbackChanges()
                error = new CommonError(message: 'Payment amount is wrong')
                this
                
            } else if (actualAmount == Money.ZERO) {

                createPaymentModel.paymentIn.type = PaymentType.INTERNAL
                PaymentInSucceed.valueOf(createPaymentModel.model).perform()
                context.commitChanges()
                response =  new PaymentResponse().with { r ->
                    r.paymentReference = createPaymentModel.paymentIn.clientReference
                    r.sessionId =  createPaymentModel.paymentIn.sessionId
                    r.paymentStatus = PaymentStatus.SUCCESSFUL
                    r.applicationIds = createPaymentModel.applications.collect { it.id.toString() }
                    r
                }
                this
            } else {
                context.commitChanges()
                context.setUserProperty('replicationEnabled', false)
                new NewPaymentExpressGatewayService(context, true).submit(createPaymentModel.model, new ish.oncourse.services.payment.PaymentRequest().with { r ->
                    r.sessionId = paymentRequest.sessionId
                    r.name = paymentRequest.creditCardName
                    r.number = paymentRequest.creditCardNumber
                    r.cvv = paymentRequest.creditCardCvv
                    r.year = paymentRequest.expiryYear
                    r.month = paymentRequest.expiryMonth
                    r
                })
                
                response = new GetPaymentStatus(context, paymentRequest.sessionId ).get()
                this
            }
        }
        
    }
}
