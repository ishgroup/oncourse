/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.entity.mixins

import groovy.transform.CompileDynamic
import ish.math.Money
import ish.oncourse.API
import ish.oncourse.server.cayenne.Document
import ish.oncourse.server.cayenne.Invoice
import ish.oncourse.server.cayenne.InvoiceAttachmentRelation
import ish.oncourse.server.cayenne.InvoiceLine

@CompileDynamic
class InvoiceMixin {

	/**
	* Prints 'Tax Invoice' or 'Credit Note' depending on the amount owing on this invoive
	*
	* @return invoice print heading
	*/
	@API
	static getPrintHeading(Invoice self) {
		return self.total >= Money.ZERO() ? "Tax Invoice" : "Credit Note"
	}

	/**
	* @return
	*/
	@API
	static hasTaxableInvoiceLines(Invoice self) {
		return self.invoiceLines.find { InvoiceLine il -> il.taxableAccount } != null
	}

	@API
	static hasNonTaxableInvoiceLines(Invoice self) {
		return self.invoiceLines.find { InvoiceLine il -> !il.taxableAccount } != null
	}

	/**
	 * @param self
	 * @return all unpaid InvoiceDueDates for this Invoice
	 */
	@API
	static getUnpaidInvoiceDueDates(Invoice self) {
		def unpaidInvoiceDueDates = []
		Money amount = self.amountPaid
		self.invoiceDueDates.sort { it.dueDate } each { it ->
			if (amount < it.amount) {
				unpaidInvoiceDueDates.add(it)
			}
			amount = amount.subtract(it.amount)
		}
		unpaidInvoiceDueDates
	}

}
