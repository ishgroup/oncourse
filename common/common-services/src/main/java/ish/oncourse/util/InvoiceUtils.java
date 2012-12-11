package ish.oncourse.util;

import ish.math.Money;
import ish.oncourse.model.Contact;
import ish.oncourse.model.Invoice;

import java.util.List;

public class InvoiceUtils {

	/**
	 * sums all invoice amount owing for a payer
	 *
	 * @param payer to be analysed
	 * @return Money sum of amount owing
	 */
	public static Money amountOwingForPayer(Contact payer) {
		Money result = Money.ZERO;
		// update invoice owing
		if (payer != null) {
			List<Invoice> invoices = payer.getInvoices();
			for (Invoice invoice : invoices) {
				result = result.add(invoice.getAmountOwing());
			}
		}
		return result;
	}

}
