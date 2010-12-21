package ish.oncourse.model;

import ish.common.payable.TaxInterface;
import ish.math.Money;
import ish.oncourse.model.auto._InvoiceLine;

public class InvoiceLine extends _InvoiceLine {

	/**
	 * @see ish.common.payable.IInvoiceLineInterface#getInvoiceTax()
	 */
	@Override
	public TaxInterface getInvoiceTax() {
		return new FakeTax();
	}
	
}
