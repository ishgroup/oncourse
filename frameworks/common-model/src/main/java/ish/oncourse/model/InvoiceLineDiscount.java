package ish.oncourse.model;

import ish.oncourse.model.auto._InvoiceLineDiscount;

public class InvoiceLineDiscount extends _InvoiceLineDiscount implements Queueable {

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId().getIdSnapshot().get(ID_PK_COLUMN) : null;
	}

}
