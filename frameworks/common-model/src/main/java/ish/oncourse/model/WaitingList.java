package ish.oncourse.model;

import ish.oncourse.model.auto._WaitingList;

public class WaitingList extends _WaitingList implements Queueable {

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ?
				(Long) getObjectId().getIdSnapshot().get(ID_PK_COLUMN) : null;	
	}
	
}
