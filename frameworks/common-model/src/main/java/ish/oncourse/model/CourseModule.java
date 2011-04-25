package ish.oncourse.model;

import ish.oncourse.model.auto._CourseModule;

public class CourseModule extends _CourseModule implements Queueable {

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN) : null;
	}
}
