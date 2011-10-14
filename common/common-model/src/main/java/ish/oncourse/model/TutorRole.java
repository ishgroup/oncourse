package ish.oncourse.model;

import ish.oncourse.model.auto._TutorRole;

public class TutorRole extends _TutorRole implements Queueable {

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId().getIdSnapshot().get(ID_PK_COLUMN) : null;
	}

}
