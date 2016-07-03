package ish.oncourse.portal.services.payment

import ish.common.types.PaymentStatus

/**
 * User: akoiro
 * Date: 3/07/2016
 */
class Response {
    def Double amount
    def Date dateDue
    def Long invoiceId
    def Long paymentId
    def PaymentStatus paymentStatus
    def Status status
    def ValidationResult validationResult = new ValidationResult()

    public static Response valueOf(Context context) {
        Response response = new Response().with {
            it.amount = context.invoice?.amountOwing?.doubleValue()
            it.dateDue = context.invoice?.dateDue
            it.invoiceId = context.invoice?.id
            it.paymentId = context.paymentIn?.id
            it.paymentStatus = context.paymentIn?.status
            return it
        }
        return response
    }
}
