package ish.oncourse.model;

import ish.oncourse.model.auto._Taggable;

public class Taggable extends _Taggable implements Queueable {

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId().getIdSnapshot().get(ID_PK_COLUMN) : null;
	}
}
