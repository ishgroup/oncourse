package ish.oncourse.willow.checkout.payment

import ish.common.types.PaymentType
import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.services.paymentexpress.NewPaymentExpressGatewayService
import ish.oncourse.util.payment.PaymentInAbandon
import ish.oncourse.util.payment.PaymentInModel
import ish.oncourse.util.payment.PaymentInSucceed
import ish.oncourse.willow.model.checkout.payment.PaymentRequest
import ish.oncourse.willow.model.checkout.payment.PaymentResponse
import ish.oncourse.willow.model.checkout.payment.PaymentStatus
import ish.oncourse.willow.model.common.CommonError
import org.apache.cayenne.ObjectContext

class ProcessPaymentModel {
    
    ObjectContext context
    ObjectContext nonReplicatedContext
    College college
    CreatePaymentModel createPaymentModel
    PaymentRequest paymentRequest
    
    
    CommonError error
    PaymentResponse response

    ProcessPaymentModel(ObjectContext context, ObjectContext nonReplicatedContext, College college, CreatePaymentModel createPaymentModel, PaymentRequest paymentRequest) {
        this.context = context
        this.nonReplicatedContext = nonReplicatedContext
        this.college = college
        this.createPaymentModel = createPaymentModel
        this.paymentRequest = paymentRequest
    }

    ProcessPaymentModel process() {
        if (createPaymentModel.applicationsOnly) {
            saveApplications()
        } else {
            Money actualAmount = createPaymentModel.paymentIn.amount
            if (actualAmount != new Money(paymentRequest.payNow)) {
                context.rollbackChanges()
                error = new CommonError(message: 'Payment amount is wrong')
                this
            } else if (actualAmount == Money.ZERO) {
                saveZeroPayment()
            } else {
                performGatewayOperation()
            }
        }
    }

    private ProcessPaymentModel saveApplications() {
        context.commitChanges()

        response =  new PaymentResponse().with { r ->
            r.status = PaymentStatus.SUCCESSFUL
            r.reference =  createPaymentModel.applications.collect { it.id.toString() }.join(', ')
            r
        }
        this
    }

    private ProcessPaymentModel saveZeroPayment() {
        createPaymentModel.paymentIn.type = PaymentType.INTERNAL
        PaymentInSucceed.valueOf(createPaymentModel.model).perform()
        context.commitChanges()
        response =  new PaymentResponse().with { r ->
            r.reference = createPaymentModel.paymentIn.clientReference
            r.sessionId =  createPaymentModel.paymentIn.sessionId
            r.status = PaymentStatus.SUCCESSFUL
            r
        }
        this
    }

    private ProcessPaymentModel performGatewayOperation() {
        context.commitChanges()
        
        PaymentInModel model = createPaymentModel.model
        new NewPaymentExpressGatewayService(nonReplicatedContext).submit(createPaymentModel.model, new ish.oncourse.services.payment.PaymentRequest().with { r ->
            r.sessionId = paymentRequest.sessionId
            r.name = paymentRequest.creditCardName
            r.number = paymentRequest.creditCardNumber
            r.cvv = paymentRequest.creditCardCvv
            r.year = paymentRequest.expiryYear
            r.month = paymentRequest.expiryMonth
            r
        })

        response = new GetPaymentStatus(context, college, paymentRequest.sessionId).get()

        if (PaymentStatus.FAILED == response.status) {
            PaymentInAbandon.valueOf(model, false).perform()
            context.commitChanges()
        }

        this
    }
    
    
}
