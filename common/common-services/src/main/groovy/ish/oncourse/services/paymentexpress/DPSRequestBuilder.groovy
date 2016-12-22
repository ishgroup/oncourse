package ish.oncourse.services.paymentexpress

import com.paymentexpress.stubs.TransactionDetails
import ish.oncourse.model.College
import ish.oncourse.model.PaymentIn
import ish.oncourse.model.PaymentOut
import ish.oncourse.services.payment.PaymentRequest

class DPSRequestBuilder {

	def String billingId
	def PaymentIn paymentIn
	def PaymentOut paymentOut
 	def College college
	def String ref
	def PaymentRequest request

	def static DPSRequestBuilder valueOf(PaymentIn paymentIn, String billingId) {
		new DPSRequestBuilder(paymentIn:paymentIn,
				college:paymentIn.college,
				billingId: billingId,
				ref:paymentIn.clientReference)
	}

	def static DPSRequestBuilder valueOf(PaymentIn paymentIn,  PaymentRequest request) {
		new DPSRequestBuilder(paymentIn:paymentIn,
				college:paymentIn.college,
				request: request,
				ref:paymentIn.clientReference)
	}
	
	
	def static DPSRequestBuilder valueOf(PaymentOut paymentOut) {
		new DPSRequestBuilder(paymentOut:paymentOut,
				college:paymentOut.college,
				ref:paymentOut.clientReference)
	}
	
	def DPSRequest build() {
		TransactionDetails details = new TransactionDetails()
		if (paymentOut) {
			details.setTxnType(PaymentExpressUtil.PAYMENT_EXPRESS_TXN_TYPE_REFUND)
			details.setAmount(PaymentExpressUtil.translateInputAmountAsDecimalString(paymentOut.getTotalAmount().toBigDecimal()))
			details.setDpsTxnRef(paymentOut.getPaymentInTxnReference())

		} else {
			details.setAmount(PaymentExpressUtil.translateInputAmountAsDecimalString(paymentIn.amount.toBigDecimal()))
			details.setTxnType(PaymentExpressUtil.PAYMENT_EXPRESS_TXN_TYPE)
			if (billingId) {
				details.setDpsBillingId(billingId)
			} else if (request) {
				details.setCardHolderName(request.name)
				details.setCardNumber(request.number)
				details.setCvc2(request.cvv)
				details.setDateExpiry(PaymentExpressUtil.translateInputExpiryDate(request.month +  "/" + request.year))
				details.setEnableAddBillCard("1")
			} else {
				details.setCardHolderName(paymentIn.getCreditCardName())
				details.setCardNumber(paymentIn.getCreditCardNumber())
				details.setCvc2(paymentIn.getCreditCardCVV())
				details.setDateExpiry(PaymentExpressUtil.translateInputExpiryDate(paymentIn.getCreditCardExpiry()))
				details.setEnableAddBillCard("1")
			}
		}
			
		details.setInputCurrency("AUD")
		details.setMerchantReference(ref)
		details.setTxnRef(ref)
		
		new DPSRequest(transactionDetails: details,
				paymentGatewayAccount: college.paymentGatewayAccount, 
				paymentGatewayPass: college.paymentGatewayPass)	
	}
	
}
