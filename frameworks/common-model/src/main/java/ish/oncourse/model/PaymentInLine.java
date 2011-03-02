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
}
