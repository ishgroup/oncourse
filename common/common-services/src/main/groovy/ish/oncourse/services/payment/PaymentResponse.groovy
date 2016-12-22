package ish.oncourse.services.payment

import groovy.transform.AutoClone

@AutoClone
class PaymentResponse {

	def String cardNameError
	def String cardNumberError
	def String cardCVVError
	def String cardExpiryDateError
	def GetPaymentState.PaymentState status

	def boolean hasErrors() {
		cardNameError || cardNumberError || cardCVVError || cardExpiryDateError 
	}


}
