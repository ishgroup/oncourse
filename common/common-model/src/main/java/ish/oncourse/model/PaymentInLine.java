package ish.oncourse.model;

import ish.oncourse.model.auto._PaymentInLine;
import ish.oncourse.utils.QueueableObjectUtils;

public class PaymentInLine extends _PaymentInLine implements Queueable {
	private static final long serialVersionUID = -6157950790523998485L;

	public College getCollege() {
		return getPaymentIn().getCollege();
	}

	public Long getId() {
		return QueueableObjectUtils.getId(this);
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
