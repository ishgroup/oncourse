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

package ish.common

import org.apache.commons.lang3.time.DateUtils

class GetInvoiceDueDate {


	private  Integer contactInvoiceTerms
	private Integer defaultInvoiceTerms

	def static GetInvoiceDueDate valueOf(Integer defaultInvoiceTerms, Integer contactInvoiceTerms)  {
		GetInvoiceDueDate getInvoiceDueDate = new GetInvoiceDueDate()
		getInvoiceDueDate.contactInvoiceTerms = contactInvoiceTerms
		getInvoiceDueDate.defaultInvoiceTerms = defaultInvoiceTerms
		return getInvoiceDueDate
	}

	public Date get() {
		Integer addDays = contactInvoiceTerms != null ? contactInvoiceTerms : defaultInvoiceTerms != null ? defaultInvoiceTerms : 0
		return DateUtils.addDays(new Date(), addDays);
	}
}
