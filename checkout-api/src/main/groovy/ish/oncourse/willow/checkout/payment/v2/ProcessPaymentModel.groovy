package ish.oncourse.willow.checkout.payment.v2

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import ish.common.types.PaymentType
import ish.math.Country
import ish.math.Money
import ish.oncourse.model.College
import ish.oncourse.model.PaymentGatewayType
import ish.oncourse.model.PaymentIn
import ish.oncourse.services.preference.GetPreference
import ish.oncourse.util.payment.PaymentInAbandon
import ish.oncourse.util.payment.PaymentInModel
import ish.oncourse.util.payment.PaymentInSucceed
import ish.oncourse.willow.checkout.payment.SetConfirmationStatus
import ish.oncourse.willow.checkout.windcave.PaymentService
import ish.oncourse.willow.checkout.windcave.SessionAttributes
import ish.oncourse.willow.model.checkout.payment.PaymentStatus
import ish.oncourse.willow.model.common.CommonError
import ish.oncourse.willow.model.v2.checkout.payment.PaymentRequest
import ish.oncourse.willow.model.v2.checkout.payment.PaymentResponse
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.commons.lang3.StringUtils

import static ish.common.types.ConfirmationStatus.DO_NOT_SEND
import static ish.common.types.ConfirmationStatus.NOT_SENT
import static ish.oncourse.services.preference.Preferences.PAYMENT_GATEWAY_TYPE
import static ish.persistence.Preferences.*

@CompileStatic
class ProcessPaymentModel {
    
    ObjectContext context
    ObjectContext nonReplicatedContext
    College college
    CreatePaymentModel createPaymentModel
    PaymentRequest paymentRequest
    String origin

    CommonError error
    PaymentResponse response

    Boolean xValidate

    ProcessPaymentModel(ObjectContext context,
                        College college,
                        CreatePaymentModel createPaymentModel,
                        PaymentRequest paymentRequest,
                        Boolean xValidate,
                        String origin) {
        this.context = context
        this.college = college
        this.xValidate = xValidate
        this.createPaymentModel = createPaymentModel
        this.paymentRequest = paymentRequest
        this.origin = origin
        this
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

        PaymentGatewayType gatewayType = PaymentGatewayType.valueOf(new GetPreference(college, PAYMENT_GATEWAY_TYPE, context).getValue())
        String apiKey
        Boolean skipAuth
        Money amount = createPaymentModel.paymentIn.amount

        switch (gatewayType) {
            case PaymentGatewayType.TEST:
                apiKey = " SXNoR3JvdXBSRVNUX0RldjphOTljYWUxYmRlZGJjNGU5ODQ3OTNmZjNhNjkwMDM5ZTdlZWUyOTgyMmQ0ZDQzZDg2M2JkZDE4NGNlOTk4NmRj"
                skipAuth = false
                break
            case PaymentGatewayType.PAYMENT_EXPRESS:
                apiKey = new GetPreference(college, PAYMENT_GATEWAY_PASS, context).getValue()
                skipAuth = new GetPreference(college, PAYMENT_GATEWAY_PURCHASE_WITHOUT_AUTH, context).getBooleanValue()
                break
            case PaymentGatewayType.DISABLED:
            default:
                throw new IllegalArgumentException()
        }
        Country country = new GetPreference(college, ACCOUNT_CURRENCY, context).getCountry()
        PaymentService paymentService = new PaymentService(apiKey, skipAuth, country)

        if (xValidate) {
            String merchantReference = UUID.randomUUID().toString()
            SessionAttributes attributes = paymentService.createSession(origin, amount, merchantReference, paymentRequest.storeCard)
            if (attributes.sessionId) {
                response = new PaymentResponse()
                response.sessionId = attributes.sessionId
                response.paymentFormUrl = attributes.ccFormUrl
                response.merchantReference = merchantReference
            } else {
                error = new CommonError(message: (attributes.errorMessage ?: PaymentService.DEFAULT_ERROR_MESSAGE))
            }
        } else {
            SessionAttributes sessionAttributes
            String merchantReference = paymentRequest.merchantReference

            if (!paymentRequest.merchantReference) {
                error = new CommonError(message: 'Merchant reference is required')
                return this
            }
            if (!paymentRequest.sessionId) {
                error = new CommonError(message: 'Payment session attribute is required')
                return this
            }
            sessionAttributes = paymentService.checkStatus(paymentRequest.sessionId)

            if (!sessionAttributes.complete) {
                error = new CommonError(message: 'Credit card authorisation is not complete')
                return this
            }

            if (!sessionAttributes.authorised) {
                error = new CommonError(message: "Credit card declined: $sessionAttributes.statusText")
                return this
            }

            if (ObjectSelect.query(PaymentIn).where(PaymentIn.GATEWAY_REFERENCE.eq(sessionAttributes.transactionId)).selectFirst(context) != null) {
                error = new CommonError(message: "Credit card payment already complete")
                return this
            }

            PaymentIn paymentIn = createPaymentModel.paymentIn
            paymentIn.creditCardExpiry = sessionAttributes.creditCardExpiry
            paymentIn.creditCardName = sessionAttributes.creditCardName
            paymentIn.creditCardNumber = sessionAttributes.creditCardNumber
            paymentIn.creditCardType = sessionAttributes.creditCardType
            paymentIn.gatewayReference = sessionAttributes.transactionId
            paymentIn.dateBanked = sessionAttributes.paymentDate
            paymentIn.billingId = sessionAttributes.billingId
            paymentIn.sessionId = merchantReference
            paymentIn.gatewayResponse = sessionAttributes.responceJson
            paymentIn.statusNotes = sessionAttributes.statusText

            if (skipAuth) {
                succeedPayment()
                return this
            } else {
                if (PaymentService.AUTH_TYPE != sessionAttributes.type) {
                    error = new CommonError(message: "Credit card transaction has wrong type")
                    return this
                }
                context.commitChanges()
                sessionAttributes = paymentService.completeTransaction(sessionAttributes.transactionId, amount, merchantReference)

                if (sessionAttributes.authorised) {
                    succeedPayment()
                } else {
                    paymentIn.gatewayResponse = sessionAttributes.responceJson
                    paymentIn.statusNotes = sessionAttributes.statusText
                    PaymentInAbandon.valueOf(createPaymentModel.model, false).perform()
                    context.commitChanges()
                    response = new PaymentResponse()
                    response.status = PaymentStatus.FAILED
                    response.responseText = sessionAttributes.statusText
                }
                return this
            }
        }

        this
    }

    private void succeedPayment() {
        PaymentInModel model = createPaymentModel.model
        PaymentInSucceed.valueOf(model).perform();

        model.paymentIn.statusNotes += 'Payment successful.'
        model.paymentIn.paymentInLines.each {  line  ->
            line.invoice.updateAmountOwing()
        }

        SetConfirmationStatus.valueOf(model).set()
        context.commitChanges()

        response = new PaymentResponse()
        response.status  = PaymentStatus.SUCCESSFUL
        response.reference = model.paymentIn.clientReference
    }

    
}
