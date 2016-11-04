package ish.oncourse.services.paymentexpress

import com.paymentexpress.stubs.TransactionDetails

class DPSRequest {
	def TransactionDetails transactionDetails
	def String paymentGatewayAccount
	def String paymentGatewayPass
}
