package ish.oncourse.model;

import ish.oncourse.model.auto._BinaryInfo;

public class BinaryInfo extends _BinaryInfo {

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN) : null;
	}
}
