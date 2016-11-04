package ish.oncourse.services.paymentexpress

import com.paymentexpress.stubs.TransactionResult2
import ish.oncourse.model.PaymentOut
import ish.oncourse.util.payment.PaymentInModel

import static ish.oncourse.services.paymentexpress.DPSResponse.ResultStatus.*

class NewTestPaymentGatewayService  implements INewPaymentGatewayService {
	@Override
	def submit(PaymentInModel model, String billingId = null) {
		new DPSResponse(status: SUCCESS, result2: new TransactionResult2(dateSettlement:new Date().toString(), dpsTxnRef: 'dpsTxnRef' ,responseText:'APPROVED.',dpsBillingId: 'dpsBillingId') )
	}


	@Override
	def submit(PaymentOut paymentOut) {
		new DPSResponse(status: SUCCESS, result2: new TransactionResult2(dateSettlement:new Date().toString(), dpsTxnRef: 'dpsTxnRef' ,responseText:'APPROVED.',dpsBillingId: 'dpsBillingId') )
	}
}

