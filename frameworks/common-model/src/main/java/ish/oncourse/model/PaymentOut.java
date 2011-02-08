package ish.oncourse.model;

import ish.oncourse.model.auto._PaymentOut;

public class PaymentOut extends _PaymentOut implements Queueable {

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ?
				(Long) getObjectId().getIdSnapshot().get(ID_PK_COLUMN) : null;
	}

}
