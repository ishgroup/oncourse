package ish.oncourse.model;

import ish.oncourse.model.auto._WebNodeContent;

public class WebNodeContent extends _WebNodeContent {
	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN) : null;
	}
}
