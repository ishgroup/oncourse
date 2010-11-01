package ish.oncourse.model;

import ish.oncourse.model.auto._WebUrlAlias;

public class WebUrlAlias extends _WebUrlAlias {
	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN) : null;
	}
}
