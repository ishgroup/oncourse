package ish.oncourse.model;

import ish.oncourse.model.auto._Module;

public class Module extends _Module {
	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN) : null;
	}
}
