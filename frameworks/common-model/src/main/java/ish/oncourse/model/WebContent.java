package ish.oncourse.model;

import ish.oncourse.model.auto._WebContent;

public class WebContent extends _WebContent {
	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN) : null;
	}
}
