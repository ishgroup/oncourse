package ish.oncourse.model;

import java.util.Date;

import ish.oncourse.model.auto._WebUrlAlias;

public class WebUrlAlias extends _WebUrlAlias {
	
	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN) : null;
	}

	@Override
	protected void onPostAdd() {
		Date today = new Date();
		setCreated(today);
		setModified(today);
	}
}
