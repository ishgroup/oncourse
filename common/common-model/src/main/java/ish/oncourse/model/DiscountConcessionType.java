package ish.oncourse.model;

import ish.oncourse.model.auto._DiscountConcessionType;

public class DiscountConcessionType extends _DiscountConcessionType implements Queueable {
	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId().getIdSnapshot().get(ID_PK_COLUMN) : null;
	}
}
