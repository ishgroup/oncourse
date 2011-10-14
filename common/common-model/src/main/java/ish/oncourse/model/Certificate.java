package ish.oncourse.model;

import ish.oncourse.model.auto._Certificate;

public class Certificate extends _Certificate implements Queueable {

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId().getIdSnapshot().get(ID_PK_COLUMN) : null;
	}

}
