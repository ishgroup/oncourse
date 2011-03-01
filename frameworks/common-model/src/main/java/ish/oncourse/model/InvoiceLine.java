package ish.oncourse.model;

import ish.common.payable.TaxInterface;
import ish.oncourse.model.auto._InvoiceLine;

public class InvoiceLine extends _InvoiceLine implements Queueable {

	/**
	 * @see ish.common.payable.IInvoiceLineInterface#getInvoiceTax()
	 */
	@Override
	public TaxInterface getInvoiceTax() {
		return new FakeTax();
	}

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN) : null;
	}
	
}
