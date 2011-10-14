package ish.oncourse.model;

import ish.oncourse.model.auto._Country;

public class Country extends _Country {
	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN) : null;
	}
}
