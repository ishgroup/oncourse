package ish.oncourse.portal.services.payment

import ish.common.types.PaymentStatus
import ish.oncourse.services.paymentexpress.PaymentExpressGatewayService
import ish.oncourse.services.paymentexpress.PaymentExpressUtil
import ish.oncourse.services.paymentexpress.PaymentInSupport
import ish.oncourse.services.paymentexpress.TransactionResult

import static ish.oncourse.portal.services.payment.ErrorMessage.invalidRequest
import static ish.oncourse.portal.services.payment.Status.result
import static ish.oncourse.portal.services.payment.Status.wait

/**
 * User: akoiro
 * Date: 3/07/2016
 */
class ProcessUpdate extends AProcess {
    @Override
    Response process() {
        validate()
        if (response.validationResult.valid()) {
            if (context.paymentIn.status == PaymentStatus.IN_TRANSACTION) {
                TransactionResult result = context.paymentGatewayService.checkPaymentTransaction(context.paymentIn)
                if (PaymentExpressUtil.isValidResult(result)) {
                    if (TransactionResult.ResultStatus.SUCCESS.equals(result.getStatus())) {
                        context.paymentIn.setStatusNotes(PaymentExpressGatewayService.SUCCESS_PAYMENT_IN);
                        context.paymentIn.setStatus(PaymentStatus.SUCCESS)
                    } else {
                        context.paymentIn.setStatusNotes(PaymentExpressGatewayService.FAILED_PAYMENT_IN);
                    }
                    PaymentInSupport.AdjustPaymentIn.valueOf(context.paymentIn, result).adjust()
                    PaymentInSupport.AdjustPaymentTransaction.valueOf(context.paymentTransaction, result).adjust()
                }
            }

            response = Response.valueOf(context)

            if (context.paymentIn.status == PaymentStatus.IN_TRANSACTION) {
                response.status = wait
            } else {
                response.paymentStatus = context.paymentIn.status
                response.status = result
            }
        }
        return null
    }


    public void validate() {
        if (request.paymentInId == null || request.invoiceId == null) {
            response.validationResult.error = invalidRequest
            return
        }
    }
}
