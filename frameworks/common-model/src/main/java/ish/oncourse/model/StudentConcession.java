package ish.oncourse.model;

import ish.oncourse.model.auto._StudentConcession;


public class StudentConcession extends _StudentConcession {

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN) : null;
	}

}
