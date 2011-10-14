package ish.oncourse.model;

import ish.oncourse.model.auto._Site;

public class Site extends _Site implements Queueable {
	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN) : null;
	}

	public boolean isHasCoordinates() {
		return getLatitude() != null && getLongitude() != null;
	}
}
