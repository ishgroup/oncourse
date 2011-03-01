package ish.oncourse.model;

import ish.oncourse.model.auto._Attendance;

public class Attendance extends _Attendance implements Queueable {

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN) : null;
	}
	
}
