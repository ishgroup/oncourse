records.each { PaymentIn pi ->
	csv << [
			"id"              : pi.id,
			"payerLastName"   : pi.payer.lastName,
			"payerFirstName"  : pi.payer.firstName,
			"payerEmail"      : pi.payer.email,
			"modifiedOn"      : pi.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"),
			"createdOn"       : pi.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"),
			"amount"          : pi.amount?.toPlainString(),
			"dateBanked"      : pi.dateBanked?.format("yyyy-MM-dd"),
			"reconciled"      : pi.reconciled,
			"source"          : pi.source.displayName,
			"status"          : pi.status.displayName,
			"type"            : pi.paymentMethod.name,
			"creditCardExpiry": pi.creditCardExpiry,
			"creditCardType"  : pi.creditCardType,
			"gatewayReference": pi.gatewayReference,
			"gatewayResponse" : pi.gatewayResponse,
			"chequeBank"      : pi.chequeBank,
			"chequeBranch"    : pi.chequeBranch,
			"chequeDrawer"    : pi.chequeDrawer,
			"privateNotes"    : pi.privateNotes,
	]
}
