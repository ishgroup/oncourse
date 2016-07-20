package ish.oncourse.portal.services.payment

import ish.common.types.PaymentStatus
import ish.math.Money
import ish.oncourse.model.PaymentIn

import static ish.oncourse.portal.services.payment.ErrorMessage.invalidRequest
import static ish.oncourse.portal.services.payment.Status.result
import static ish.oncourse.portal.services.payment.Status.wait
import static ish.oncourse.portal.services.payment.WarningMessage.thereIsPaymentInTransaction
import static ish.oncourse.portal.services.payment.WarningMessage.thisInvoiceAlreadyPaid

/**
 * User: akoiro
 * Date: 3/07/2016
 */
class ProcessMake extends AProcess {
    def Closure<PaymentIn> createPaymentInClosure
    def Closure<PaymentIn> processPaymentInClosure


    @Override
    public Response process() {
        validate()
        if (response.validationResult.valid()) {
            def paymentIn = createPaymentInClosure.call(request)
            paymentIn = processPaymentInClosure.call(paymentIn)
            response = Response.valueOf(context)
            response.paymentId = paymentIn.id
            response.paymentStatus = paymentIn.status

            if (paymentIn.status == PaymentStatus.IN_TRANSACTION) {
                response.status = wait
            } else {
                response.status = result
            }
        }
        return response
    }

    public void validate() {
        ValidateCreditCard validateCreditCard = new ValidateCreditCard(result: response.validationResult, card: request.card)
        validateCreditCard.validate()
        if (!response.validationResult.valid()) {
            return
        }

        if (context.invoice == null) {
            response.validationResult.error = invalidRequest
            return
        }

        if (!context.invoice.amountOwing.isGreaterThan(new Money(0, 1))) {
            response.validationResult.warning = thisInvoiceAlreadyPaid
            return
        }

        if (context.notFinalPaymentIn) {
            response.validationResult.warning = thereIsPaymentInTransaction
        }
    }
}
