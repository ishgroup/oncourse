package ish.oncourse.services.payment

import ish.oncourse.model.PaymentIn
import ish.oncourse.util.payment.PaymentInModel

class ExtendedModel extends PaymentInModel {
	
	def List<PaymentIn> failedPayments = new ArrayList<>()
	def PaymentIn successPayment = null
}
