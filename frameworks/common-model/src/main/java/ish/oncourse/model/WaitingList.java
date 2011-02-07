package ish.oncourse.model;

import ish.oncourse.model.auto._WaitingList;

public class WaitingList extends _WaitingList implements Queueable {

	private transient boolean doQueue = true;

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ?
				(Long) getObjectId().getIdSnapshot().get(ID_PK_COLUMN) : null;	
	}
	
	public boolean getDoQueue() {
		return doQueue;
	}

	public void setDoQueue(boolean doQueue) {
		this.doQueue = doQueue;
	}

}
