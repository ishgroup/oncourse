package ish.oncourse.portal.services.payment

import static ish.oncourse.portal.services.payment.ErrorMessage.invalidRequest
import static ish.oncourse.portal.services.payment.WarningMessage.thereAreNotUnbalancedInvoices

/**
 * User: akoiro
 * Date: 3/07/2016
 */
class ProcessInit extends AProcess {

    public Response process() {
        validate()

        if (response.validationResult.valid()) {
            response = Response.valueOf(context)
            response.status = Status.card
        }
        return response;
    }

    public void validate() {
        if (request.paymentInId != null || request.invoiceId != null) {
            response.validationResult.error = invalidRequest
            return
        }

        if (context.invoice == null) {
            response.validationResult.warning = thereAreNotUnbalancedInvoices
        }

    }
}
