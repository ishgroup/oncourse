records.each { PaymentOut po ->
	csv << [
			"id"              : po.id,
			"payeeLastName"   : po.payee.lastName,
			"payeeFirstName"  : po.payee.firstName,
			"payeeEmail"      : po.payee.email,
			"modifiedOn"      : po.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"),
			"createdOn"       : po.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"),
			"amount"          : po.amount?.toPlainString(),
			"dateBanked"      : po.dateBanked?.format("yyyy-MM-dd"),
			"reconciled"      : po.reconciled,
			"status"          : po.status.displayName,
			"type"            : po.paymentMethod.name,
			"creditCardExpiry": po.creditCardExpiry,
			"creditCardType"  : po.creditCardType,
			"gatewayReference": po.gatewayReference,
			"chequeBank"      : po.chequeBank,
			"chequeBranch"    : po.chequeBranch,
			"chequeDrawer"    : po.chequeDrawer,
			"privateNotes"    : po.privateNotes
	]
}
