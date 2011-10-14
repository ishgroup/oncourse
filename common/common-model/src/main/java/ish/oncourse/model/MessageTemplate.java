package ish.oncourse.model;

import ish.oncourse.model.auto._MessageTemplate;

public class MessageTemplate extends _MessageTemplate implements Queueable {

	/*
	 * (non-Javadoc)
	 * 
	 * @see ish.oncourse.model.Queueable#getId()
	 */
	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN) : null;
	}
}
