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
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.Invoice
import ish.oncourse.server.print.proxy.PrintableStatementLine
import ish.util.ContactUtils
import ish.util.UrlUtil
import org.apache.cayenne.exp.ExpressionFactory
import org.apache.cayenne.query.ObjectSelect

import java.time.LocalDate

@CompileDynamic
class ContactMixin {

    /**
     * For this contact, find all the statement lines (invoices, credit notes, payments)
     * and then filter them between the date range.
     *
     * @param self
     * @param from the start datetime to find lines
     * @param to the end datetime
     * @return a list of all statement lines, not sorted
     */
	static List<PrintableStatementLine> getStatementLines(Contact self, LocalDate from, LocalDate to) {
		def financialItems = ContactUtils.getFinancialItems(self, PrintableStatementLine)

        //TODO: sort the output by date
		return ExpressionFactory.betweenExp(PrintableStatementLine.DATE, from, to).filterObjects(financialItems)
	}

	/**
	 *
	 * Generates URL to portal login page.
	 *
	 * @return portal login page URL
	 */
	@API
	static String getPortalLoginURL(Contact self) {
		"$UrlUtil.PORTAL_URL/login${self.email ? "?e=$self.email" : ''}"
	}

	/**
	*
	* Get unbalanced invoices
	*
	* @return unbalanced invoices
	*/
	@API
	static List<Invoice> getUnbalancedInvoices(Contact self) {
		return ObjectSelect.query(Invoice)
				.where(Invoice.CONTACT.eq(self))
				.and(Invoice.AMOUNT_OWING.ne(Money.ZERO))
				.orderBy(Invoice.DATE_DUE.asc())
				.select(self.context)
	}


}
