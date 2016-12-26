package ish.oncourse.services.payment

import groovy.transform.CompileStatic

@CompileStatic
enum PaymentAction {
	MAKE_PAYMENT,
	CANCEL,
	TRY_OTHER
}
