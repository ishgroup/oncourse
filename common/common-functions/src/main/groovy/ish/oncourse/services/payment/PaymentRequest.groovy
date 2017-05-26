package ish.oncourse.services.payment

import groovy.transform.AutoClone
import groovy.transform.CompileStatic

@AutoClone
@CompileStatic
class PaymentRequest {
	String sessionId
	String name
	String number
	String cvv
	String year
	String month
	PaymentAction action
}
