package ish.oncourse.model;

import java.util.Date;

import ish.oncourse.model.auto._WebContent;

public class WebContent extends _WebContent {
	
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
