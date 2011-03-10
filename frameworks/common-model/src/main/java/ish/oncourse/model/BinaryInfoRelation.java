package ish.oncourse.model;

import ish.oncourse.model.auto._BinaryInfoRelation;

public class BinaryInfoRelation extends _BinaryInfoRelation implements Queueable {

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId().getIdSnapshot().get(ID_PK_COLUMN) : null;
	}

}
