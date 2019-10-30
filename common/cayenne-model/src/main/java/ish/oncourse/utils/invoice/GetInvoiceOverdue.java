/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.utils.invoice;

import ish.math.Money;
import ish.oncourse.model.Invoice;
import ish.oncourse.model.InvoiceDueDate;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * User: akoiro
 * Date: 25/07/2016
 */
public class GetInvoiceOverdue {
	private Invoice invoice;
	private List<InvoiceDueDate> dueDates;
	private Money amountOwing;
	private Date targetDate;

	private Money overdue;
	private Date dateDue;

	private Money next;
	private Date nextDateDue;


	private static Date getCurrentDate() {
		Date date = DateUtils.addDays(new Date(), 1);
		date = DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
		date = DateUtils.addMilliseconds(date, -1);
		return date;
	}

	public GetInvoiceOverdue call() {
		
		overdue = Money.ZERO;
		next = Money.ZERO;
		dateDue = invoice.getDateDue();
		nextDateDue = invoice.getDateDue();

		if (dueDates.isEmpty()) {
			overdue = invoice.getDateDue().before(targetDate) ? invoice.getAmountOwing() : Money.ZERO;
			next = invoice.getAmountOwing();
		} else {
			InvoiceDueDate.DUE_DATE.asc().orderList(dueDates);

			for (InvoiceDueDate dueDate : dueDates) {
				if (dueDate.getDueDate().before(targetDate)) {
					overdue = overdue.add(dueDate.getAmount());
					dateDue = dueDate.getDueDate();
				} else {
					if (next.isZero()) {
						next = GetAmountOwing.valueOf(dueDate.getInvoice()).get();
						nextDateDue = dueDate.getDueDate();
					}
				}
			}
			overdue = overdue.subtract(invoice.getTotalGst().subtract(amountOwing));
			overdue = overdue.isGreaterThan(Money.ZERO) ? overdue : Money.ZERO;
			if (overdue.isGreaterThan(Money.ZERO)) {
				next = overdue;
				nextDateDue = dateDue;
			}
		}
		return this;
	}

	public Money getOverdue() {
		return overdue;
	}

	public Money getNext() {
		return next;
	}

	public Date getNextDateDue() {
		return nextDateDue;
	}

	public Date getDateDue() {
		return dateDue;
	}


	public static GetInvoiceOverdue valueOf(Invoice invoice) {
	
		return valueOf(invoice, getCurrentDate());
	}
	
	public static GetInvoiceOverdue valueOf(Invoice invoice, Date targetDate) {
		GetInvoiceOverdue result = new GetInvoiceOverdue();
		result.invoice = invoice;
		result.targetDate = targetDate;
		result.dueDates = invoice.getInvoiceDueDates();
		result.amountOwing = GetAmountOwing.valueOf(invoice).get();
		
		return result;
	}

}
