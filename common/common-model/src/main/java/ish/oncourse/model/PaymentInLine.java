package ish.oncourse.model;

import ish.oncourse.model.auto._PaymentInLine;

public class PaymentInLine extends _PaymentInLine implements Queueable {

	public College getCollege() {
		return getPaymentIn().getCollege();
	}

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN) : null;
	}
	
	/**
	 * Creates a copy of current paymentInLine object.
	 * @return copy of paymentInLine object
	 */
	public PaymentInLine makeCopy() {
		PaymentInLine pl = getObjectContext().newObject(PaymentInLine.class);
		pl.setAmount(getAmount());
		pl.setCollege(getCollege());
		pl.setCreated(getCreated());
		pl.setInvoice(getInvoice());
		pl.setPaymentIn(getPaymentIn());
		pl.setModified(getModified());
		return pl;
	}
}
