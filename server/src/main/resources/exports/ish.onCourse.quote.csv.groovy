records.each { Quote q ->
	csv << [
			"modifiedOn"      : q.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"),
			"createdOn"       : q.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"),
			"contactLastName" : q.contact.lastName,
			"contactFirstName": q.contact.firstName,
			"total"           : q.total?.toPlainString(),
			"totalIncTax"     : q.totalIncTax?.toPlainString(),
			"totalTax"        : q.totalTax?.toPlainString(),
			"amountOwing"     : q.amountOwing?.toPlainString(),
			"billToAddress"   : q.billToAddress,
			"dateDue"         : q.dateDue?.format("yyyy-MM-dd"),
			"invoiceDate"     : q.invoiceDate?.format("yyyy-MM-dd"),
			"title"           : q.title,
			"description"     : q.description,
			"privateNotes"    : q.notes,
			"publicNotes"     : q.publicNotes,
			"shippingAddress" : q.shippingAddress,
			"createdBy"       : q.createdByUserName,
			"source"          : q.source.displayName
	]
}
