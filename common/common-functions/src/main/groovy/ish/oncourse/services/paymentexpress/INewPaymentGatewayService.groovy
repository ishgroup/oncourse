package ish.oncourse.services.paymentexpress

import groovy.transform.CompileStatic
import ish.oncourse.model.PaymentOut
import ish.oncourse.services.payment.PaymentRequest
import ish.oncourse.util.payment.PaymentInModel

@CompileStatic
interface  INewPaymentGatewayService {

	def submit(PaymentInModel model, String billingId)
	def submit(PaymentInModel model, PaymentRequest response)
	def submit(PaymentInModel model)
	def submit(PaymentOut paymentOut)
}
