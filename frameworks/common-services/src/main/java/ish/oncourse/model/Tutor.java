package ish.oncourse.model;

import ish.oncourse.model.auto._Tutor;

public class Tutor extends _Tutor {

	public Integer getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Integer) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN)
				: null;
	}

	public String getFullName() {
		return getFirstName() + " " + getLastName();
	}

}
