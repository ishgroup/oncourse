package ish.oncourse.model;

import ish.oncourse.model.auto._College;

public class College extends _College {
	public Integer getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? ((Number) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN)).intValue()
				: null;
	}
}
