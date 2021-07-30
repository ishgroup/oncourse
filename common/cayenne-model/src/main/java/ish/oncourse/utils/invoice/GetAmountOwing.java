/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.utils.invoice;

import ish.common.types.PaymentStatus;
import ish.math.Money;
import ish.oncourse.model.AbstractInvoice;
import ish.oncourse.model.AbstractInvoiceLine;
import ish.oncourse.model.InvoiceLine;
import ish.oncourse.model.PaymentInLine;

import java.util.List;

/**
 * User: akoiro
 * Date: 20/07/2016
 */
public class GetAmountOwing {
	private AbstractInvoice invoice;

	private GetAmountOwing() {
		super();
	}

	public Money get() {
		// update invoice owing
		Money totalCredit = getTotalCredit();
		Money totalInvoiced = getTotalInvoiced();
		return totalInvoiced.subtract(totalCredit);
	}

	private Money getTotalCredit() {
		Money result = Money.ZERO;
		List<PaymentInLine> paymentLines = invoice.getPaymentInLines();
		for (PaymentInLine paymentLine : paymentLines) {
			if (PaymentStatus.SUCCESS.equals(paymentLine.getPaymentIn().getStatus())) {
				result = result.add(paymentLine.getAmount());
			}
		}
		return result;
	}

	private Money getTotalInvoiced() {
		Money result = Money.ZERO;
		List<? extends AbstractInvoiceLine> invoiceLines = invoice.getLines();
		for (AbstractInvoiceLine invoiceLine : invoiceLines) {
			result = result.add(invoiceLine.getFinalPriceToPayIncTax());
		}
		return result;
	}

	public static GetAmountOwing valueOf(AbstractInvoice invoice) {
		GetAmountOwing result = new GetAmountOwing();
		result.invoice = invoice;
		return result;
	}
}
