package ish.oncourse.model;

import ish.oncourse.model.auto._Invoice;

public class Invoice extends _Invoice implements Queueable {

	private transient boolean doQueue = true;

	/**
	 * Returns the primary key property - id of {@link Invoice}.
	 * 
	 * @return
	 */
	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN) : null;
	}

	public boolean getDoQueue() {
		return doQueue;
	}

	public void setDoQueue(boolean doQueue) {
		this.doQueue = doQueue;
	}

}
