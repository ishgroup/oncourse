package ish.oncourse.model;

import ish.oncourse.model.auto._SessionTutor;

public class SessionTutor extends _SessionTutor implements Queueable {
	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId().getIdSnapshot().get(ID_PK_COLUMN) : null;
	}
}
