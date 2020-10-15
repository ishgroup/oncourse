/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

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
			"invoiceNumber"   : pi.invoices.invoiceNumber,
			"bankAccount"     : pi.invoices.invoiceLines.account.accountCode,
			"incomeAccount"   : pi.account.description,
			"taxCode"    	  : pi.invoices.invoiceLines.tax.description,
	]
}
