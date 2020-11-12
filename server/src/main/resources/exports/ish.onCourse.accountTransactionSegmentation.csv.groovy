records.each { AccountTransaction t ->
	csv << [
			"id"                 : t.id,
			"Transaction date"   : t.transactionDate?.format("d-M-y"),
			"Account code"       : '"' + t.account.accountCode +'"',
			"Account description": t.account.description,
			"Account type"       : t.account.type.displayName,
			"Amount"             : t.amount?.toPlainString(),
			"Source"             : t.source,
			"Invoice number"     : t.invoiceNumber,
			"Invoice description" : t.relatedInvoiceLine?.description,
			"Invoice account"   : '"' + t.relatedInvoiceLine?.account?.accountCode + '"',
			"Contact name"       : t.contactName,
	]
}
