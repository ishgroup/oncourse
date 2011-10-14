package ish.oncourse.model;

import ish.oncourse.model.auto._MessagePerson;

public class MessagePerson extends _MessagePerson implements Queueable {

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
