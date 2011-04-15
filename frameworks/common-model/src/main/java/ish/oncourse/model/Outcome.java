package ish.oncourse.model;

import ish.oncourse.model.auto._Outcome;

public class Outcome extends _Outcome implements Queueable {

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN) : null;
	}

}
