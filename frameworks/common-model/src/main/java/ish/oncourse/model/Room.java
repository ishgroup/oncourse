package ish.oncourse.model;

import ish.oncourse.model.auto._Room;

public class Room extends _Room implements Queueable {

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId().getIdSnapshot().get(ID_PK_COLUMN) : null;
	}

}
