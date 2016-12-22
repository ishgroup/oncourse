package ish.oncourse.services.payment

import groovy.transform.AutoClone

@AutoClone
class PaymentRequest {
	def String sessionId
	def String name
	def String number
	def String cvv
	def String year
	def String month
	def PaymentAction action
}
