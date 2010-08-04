package ish.oncourse.model;

import ish.oncourse.model.auto._Tutor;

public class Tutor extends _Tutor {

	public Integer getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? ((Number) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN)).intValue()
				: null;
	}

	public String getFullName() {
		return getFirstName() + " " + getLastName();
	}

}
