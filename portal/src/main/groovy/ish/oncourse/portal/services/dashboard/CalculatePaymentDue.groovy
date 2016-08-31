package ish.oncourse.portal.services.dashboard

import groovy.time.TimeCategory
import ish.math.Money
import ish.oncourse.model.Contact
import ish.oncourse.model.Invoice
import ish.oncourse.utils.invoice.GetInvoiceOverdue
import org.apache.cayenne.query.ObjectSelect

class CalculatePaymentDue {
	
	def Contact contact;
	def List<Invoice>  owingInvoices;
	def Date next7Days
	
	
	def CalculatePaymentDue(Contact contact) {
		this.contact = contact
		
		use(TimeCategory) {
			next7Days = new Date() + 8.days
			next7Days.clearTime()
			next7Days = next7Days - 1.second
		}
	}
	
	def Money calculate() {
		Money owing = Money.ZERO
		getOwingInvoices().each { invoice ->  owing = owing.add(GetInvoiceOverdue.valueOf(invoice, next7Days).call().overdue) }
		return owing;
	}
	
	def List<Invoice> getOwingInvoices() {
		if (!contact) {
			return Collections.EMPTY_LIST
		} else if (owingInvoices == null) {
			
			owingInvoices =  ObjectSelect.query(Invoice).where(Invoice.CONTACT.eq(contact))
					.and(Invoice.AMOUNT_OWING.gt(Money.ZERO))
					.and(Invoice.DATE_DUE.lt(next7Days)).select(contact.getObjectContext())
			return owingInvoices
		}
		return owingInvoices
	}
	 
}
