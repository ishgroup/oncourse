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
