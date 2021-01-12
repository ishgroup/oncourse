records.each { Banking banking ->
	csv << [
			"Reconciled" : banking.reconciledStatus,
			"Date"       : banking.settlementDate?.format("d-M-y HH:mm:ss"),
			"Type"       : banking.type,
			"Site"       : banking.adminSite?.name,
			"User"       : banking.createdBy?.email,
			"Total"      : banking.total?.toPlainString(),
	]
}
