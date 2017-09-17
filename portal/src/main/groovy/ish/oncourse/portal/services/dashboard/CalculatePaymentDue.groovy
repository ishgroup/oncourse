package ish.oncourse.portal.services.dashboard

import groovy.time.TimeCategory
import ish.math.Money
import ish.oncourse.model.Contact
import ish.oncourse.model.Invoice
import ish.oncourse.utils.invoice.GetInvoiceOverdue
import org.apache.cayenne.query.ObjectSelect

import static ish.oncourse.portal.services.dashboard.CalculateAttendancePercent.DASHBOARD_CACHE
import static org.apache.cayenne.query.QueryCacheStrategy.SHARED_CACHE

class CalculatePaymentDue {

    private Contact contact
    private List<Invoice> owingInvoices
    private Date next7Days


    CalculatePaymentDue(Contact contact) {
		this.contact = contact
		
		use(TimeCategory) {
			next7Days = new Date() + 8.days
			next7Days.clearTime()
			next7Days = next7Days - 1.second
		}
	}

    Money calculate() {
		Money owing = Money.ZERO
		getOwingInvoices().each { invoice ->  owing = owing.add(GetInvoiceOverdue.valueOf(invoice, next7Days).call().overdue) }
        return owing
	}

    List<Invoice> getOwingInvoices() {
		if (!contact) {
			return Collections.EMPTY_LIST
		} else if (owingInvoices == null) {
			
			owingInvoices =  ObjectSelect.query(Invoice).where(Invoice.CONTACT.eq(contact))
					.and(Invoice.AMOUNT_OWING.gt(Money.ZERO))
					.and(Invoice.DATE_DUE.lt(next7Days))
					.prefetch(Invoice.INVOICE_LINES.disjoint())
					.prefetch(Invoice.PAYMENT_IN_LINES.disjoint())
					.prefetch(Invoice.INVOICE_DUE_DATES.disjoint())
                    .cacheStrategy(SHARED_CACHE, DASHBOARD_CACHE)
					.select(contact.getObjectContext())
			return owingInvoices
		}
		return owingInvoices
	}
	 
}
