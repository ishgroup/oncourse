records.collectMany { Quote q -> q.quoteLines }.each { ql ->
	csv << [
			"Quote title"                 : ql.quote.title,
			"Quote Date"                  : ql.invoice.invoiceDate?.format("yyyy-MM-dd"),
			"Quote line Title"            : ql.title,
			"Quote line Description"      : ql.description,
			"Quote To Last Name"          : ql.quote.contact.lastName,
			"Quote To First Name"         : ql.quote.contact.firstName,
			"Quote line Account Code"     : ql.account.accountCode,
			"Quote line Account name"     : ql.account.description,
			"Quote line amount"           : ql.priceTotalIncTax?.toPlainString(),
			"Quote line Tax"              : ql.totalTax?.toPlainString(),
			"Tax Type"                      : ql.tax.taxCode,
			"Quote line discount"         : ql.discountTotalExTax?.toPlainString(),
			"Quote Owing"                 : ql.quote.amountOwing?.toPlainString(),
			"Quote prepaid fees remaining": ql.prepaidFeesRemaining?.toPlainString(),
			"Quote line Quantity"         : ql.quantity,
			"Quote description"           : ql.quote.description,
			"Quote Due Date"              : ql.quote.dateDue?.format("yyyy-MM-dd"),
			"Quote Created by"            : ql.quote.createdByUserName,
			"Quote Source"                : ql.quote.source.displayName,
			"Quote modified on"           : ql.quote.modifiedOn?.format("d/M/Y hh:mm"),
			"Quote created on"            : ql.quote.createdOn?.format("d/M/Y hh:mm")
	]
}
