package ish.oncourse.model;

import ish.oncourse.model.auto._ConcessionType;

public class ConcessionType extends _ConcessionType implements Queueable {

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN) : null;
	}

}
