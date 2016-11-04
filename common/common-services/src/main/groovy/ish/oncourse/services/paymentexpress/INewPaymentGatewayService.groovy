package ish.oncourse.services.paymentexpress

import ish.oncourse.model.PaymentOut
import ish.oncourse.util.payment.PaymentInModel

public interface  INewPaymentGatewayService {

	def submit(PaymentInModel model, String billingId)
	def submit(PaymentInModel model)
	def submit(PaymentOut paymentOut)
}
