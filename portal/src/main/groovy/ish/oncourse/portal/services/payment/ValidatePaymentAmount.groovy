package ish.oncourse.portal.services.payment

import ish.math.Money

import static ish.oncourse.portal.services.payment.WarningMessage.*

class ValidatePaymentAmount {

	def Request request
	def Context context
	def ValidationResult result

	public void validate() {
		if (!request.card.amount) {
			result.warning = amountWrong
			return
		}
		
		Money twentyDollars = new Money(20, 0)
		Money actualOwing = context.invoice.amountOwing
		Money amountPaid = new Money(request.card.amount.toString())

		if (!actualOwing.isGreaterThan(new Money(0, 1))) {
			result.warning = thisInvoiceAlreadyPaid
			return
		}

		//ot allow invoice to be overpaid.
		if (amountPaid.isGreaterThan(actualOwing)) {
			result.warning = amountMoreThanOwing
			return
		}
		
		//payment amount limited to a minimum of $20, or the amount of the total owing if that is less than $20.
		if (actualOwing.isLessThan(twentyDollars) && amountPaid.isLessThan(actualOwing)) {
			result.warning = amountLessThanOwing
		} else if (amountPaid.isLessThan(twentyDollars)) {
			result.warning = amountLessThan20Dollars
		}
	}
}
