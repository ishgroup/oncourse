records.each { Invoice i ->
	csv << [
			"modifiedOn"      : i.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"),
			"createdOn"       : i.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"),
			"contactLastName" : i.contact.lastName,
			"contactFirstName": i.contact.firstName,
			"total"           : i.total?.toPlainString(),
			"totalIncTax"     : i.totalIncTax?.toPlainString(),
			"totalTax"        : i.totalTax?.toPlainString(),
			"amountOwing"     : i.amountOwing?.toPlainString(),
			"billToAddress"   : i.billToAddress,
			"dateDue"         : i.dateDue?.format("yyyy-MM-dd"),
			"invoiceDate"     : i.invoiceDate?.format("yyyy-MM-dd"),
			"invoiceNumber"   : i.invoiceNumber,
			"privateNotes"    : i.notes,
			"publicNotes"     : i.publicNotes,
			"shippingAddress" : i.shippingAddress,
			"createdBy"       : i.createdByUserName,
			"source"          : i.source.displayName
	]
}
