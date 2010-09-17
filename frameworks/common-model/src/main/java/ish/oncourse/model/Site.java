package ish.oncourse.model;

import ish.oncourse.model.auto._Site;

public class Site extends _Site {
	public Integer getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? ((Number) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN)).intValue()
				: null;
	}

	public boolean isHasCoordinats() {
		return getLatitude() != null && getLongitude() != null;
	}
}
