package ish.oncourse.services.paymentexpress

import groovy.transform.CompileStatic
import ish.oncourse.model.IPaymentTransaction
import ish.oncourse.model.PaymentOutTransaction

import static ish.oncourse.services.paymentexpress.DPSResponse.ResultStatus.*

@CompileStatic
class ProcessPaymentTransaction {

	IPaymentTransaction transaction
	DPSResponse response

	static ProcessPaymentTransaction valueOf(IPaymentTransaction transaction, DPSResponse response) {
		return new ProcessPaymentTransaction(response: response, transaction: transaction)
	}

	def process() {
		if (response.result2 != null) {
			transaction.setSoapResponse(response.result2.merchantHelpText);
			transaction.setResponse(response.result2.responseText);
			transaction.setTxnReference(response.result2.txnRef);
		}
		if (transaction instanceof PaymentOutTransaction) {
			transaction.isFinalised = true
		} else {
			switch (response.status) {
				case SUCCESS:
				case RETRY:
				case FAILED:
					transaction.isFinalised = true
					break
				default:
					transaction.isFinalised = false
			}
		}
		
		transaction.objectContext.commitChanges()
	}
}
