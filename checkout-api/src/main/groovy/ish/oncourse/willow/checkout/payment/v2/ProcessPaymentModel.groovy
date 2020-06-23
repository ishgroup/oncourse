package ish.oncourse.willow.checkout.payment.v2

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import ish.common.types.PaymentType
import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.PaymentGatewayType
import ish.oncourse.services.paymentexpress.INewPaymentGatewayService
import ish.oncourse.services.paymentexpress.NewDisabledPaymentGatewayService
import ish.oncourse.services.paymentexpress.NewPaymentExpressGatewayService
import ish.oncourse.services.paymentexpress.NewTestPaymentGatewayService
import ish.oncourse.services.preference.GetPreference
import ish.oncourse.util.payment.PaymentInAbandon
import ish.oncourse.util.payment.PaymentInModel
import ish.oncourse.util.payment.PaymentInSucceed
import ish.oncourse.willow.checkout.payment.GetPaymentStatus
import ish.oncourse.willow.checkout.payment.SetConfirmationStatus
import ish.oncourse.willow.checkout.windcave.PaymentService
import ish.oncourse.willow.model.checkout.payment.PaymentStatus
import ish.oncourse.willow.model.common.CommonError
import ish.oncourse.willow.model.v2.checkout.payment.PaymentRequest
import ish.oncourse.willow.model.v2.checkout.payment.PaymentResponse
import org.apache.cayenne.ObjectContext

import static ish.oncourse.services.preference.Preferences.PAYMENT_GATEWAY_TYPE

@CompileStatic
class ProcessPaymentModel {
    
    ObjectContext context
    ObjectContext nonReplicatedContext
    College college
    CreatePaymentModel createPaymentModel
    PaymentRequest paymentRequest


    CommonError error
    PaymentResponse response

    Boolean xValidate

    ProcessPaymentModel(ObjectContext context,
                        College college,
                        CreatePaymentModel createPaymentModel,
                        PaymentRequest paymentRequest,
                        Boolean xValidate,
                        PaymentService paymentService) {
        this.context = context
        this.college = college
        this.xValidate = xValidate
        this.createPaymentModel = createPaymentModel
        this.paymentRequest = paymentRequest
    }
    
    @CompileStatic(TypeCheckingMode.SKIP)
    ProcessPaymentModel process() {
        if (createPaymentModel.noPayment) {
            saveItems()
        } else {
            Money actualAmount = createPaymentModel.paymentIn.amount
            if (actualAmount != (paymentRequest.ccAmount?.toMoney() ?: Money.ZERO)) {
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

    private ProcessPaymentModel saveItems() {
        context.commitChanges()
        response =  new PaymentResponse()

        if (!xValidate) {
            response.status = PaymentStatus.SUCCESSFUL
            List<String> ids = createPaymentModel.applications.collect { it.id.toString() }
            ids += createPaymentModel.waitingLists.collect { it.id.toString() }
            response.reference = ids.join(', ')
        }

        this
    }

    private ProcessPaymentModel saveZeroPayment() {
        createPaymentModel.paymentIn.type = PaymentType.INTERNAL

        PaymentInSucceed.valueOf(createPaymentModel.model).perform()
        SetConfirmationStatus.valueOf(createPaymentModel.model).set()
        context.commitChanges()
        response =  new PaymentResponse()
        if (!xValidate) {
            response.reference = createPaymentModel.paymentIn.clientReference
            response.status = PaymentStatus.SUCCESSFUL
        }
        this
    }

    private ProcessPaymentModel performGatewayOperation() {
        context.commitChanges()

        if (xValidate) {

        } else {

        }
        
//        PaymentInModel model = createPaymentModel.model
//
//        paymentGatewayService.submit(createPaymentModel.model, new ish.oncourse.services.payment.PaymentRequest().with { r ->
//            r.sessionId = paymentRequest.sessionId
//            r.name = paymentRequest.creditCardName
//            r.number = paymentRequest.creditCardNumber
//            r.cvv = paymentRequest.creditCardCvv.trim()
//            r.year = paymentRequest.expiryYear
//            r.month = paymentRequest.expiryMonth
//            r
//        })
//
//        response = new GetPaymentStatus(context, college, paymentRequest.sessionId).get(model.paymentIn)
//        if (PaymentStatus.FAILED == response.status) {
//            PaymentInAbandon.valueOf(model, false).perform()
//        } else if (PaymentStatus.SUCCESSFUL == response.status) {
//            SetConfirmationStatus.valueOf(model).set()
//        }
//
//        context.commitChanges()
        this
    }
    
    private INewPaymentGatewayService getPaymentGatewayService() {
        PaymentGatewayType gatewayType = PaymentGatewayType.valueOf(new GetPreference(college, PAYMENT_GATEWAY_TYPE, context).getValue())

        switch (gatewayType) {
            case PaymentGatewayType.DISABLED:
                return new NewDisabledPaymentGatewayService()
                break
            case PaymentGatewayType.TEST:
                return new NewTestPaymentGatewayService(nonReplicatedContext)
                break
            case PaymentGatewayType.PAYMENT_EXPRESS:
                return new NewPaymentExpressGatewayService(nonReplicatedContext)
                break
            default:
                return new NewDisabledPaymentGatewayService()
        }
    }
    
}
