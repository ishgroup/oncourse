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

import ish.math.Money
import ish.oncourse.API
import ish.oncourse.server.cayenne.CorporatePass
import ish.util.InvoiceUtil

class CorporatePassMixin {


	/**
	* @return sum of invoice totals across all linked invoices
	*/
	@API
	static getTotalAmount(CorporatePass self) {
		return InvoiceUtil.sumInvoices(self.invoices)
	}

	/**
	* @return sum of total amount owing across all linked invoices
	*/
	@API
	static getTotalAmountOwing(CorporatePass self) {
		return self.invoices.inject(Money.ZERO()) { Money total, invoice -> total.add(invoice.amountOwing) }
	}

	/**
	* @return all related classes unique codes in a comma separated list
	*/
	@API
	static getLinkedClassesList(CorporatePass self) {
		return self.validClasses.collect { courseClass -> courseClass.uniqueCode }.join(", ")
	}

	/**
	* @return collection of all invoices lines from linked invoices
	*/
	@API
	static getInvoiceLines(CorporatePass self) {
		return self.invoices.collectMany { invoice -> invoice.invoiceLines }
	}

	/**
	 * @return how many times this corporate pass used
	 */
	@API
	static getTimesUsed(CorporatePass self) {
		return self.invoices.size()
	}
}
