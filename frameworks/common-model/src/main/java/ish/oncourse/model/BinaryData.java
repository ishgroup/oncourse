package ish.oncourse.model;

import ish.oncourse.model.auto._BinaryData;

public class BinaryData extends _BinaryData implements Queueable {

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId().getIdSnapshot().get(ID_PK_COLUMN) : null;
	}

	public College getCollege() {
		// TODO Auto-generated method stub
		return null;
	}

}
