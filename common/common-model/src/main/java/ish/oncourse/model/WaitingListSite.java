package ish.oncourse.model;

import java.util.Date;

import ish.oncourse.model.auto._WaitingListSite;

public class WaitingListSite extends _WaitingListSite implements Queueable {

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN) : null;
	}

	public void setCreated(Date created) {
	}

	public void setModified(Date modified) {
		getWaitingList().setModified(modified);
	}
}
