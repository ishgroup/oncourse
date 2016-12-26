package ish.oncourse.services.payment

import groovy.transform.CompileStatic
import ish.oncourse.model.PaymentIn
import ish.oncourse.util.payment.PaymentInModel

@CompileStatic
class ExtendedModel extends PaymentInModel {
	
	List<PaymentIn> failedPayments = new ArrayList<>()
	PaymentIn successPayment = null
}
