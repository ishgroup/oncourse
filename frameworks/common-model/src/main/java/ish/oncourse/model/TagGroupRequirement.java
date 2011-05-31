package ish.oncourse.model;

import ish.oncourse.model.auto._TagGroupRequirement;

public class TagGroupRequirement extends _TagGroupRequirement implements Queueable {

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId()
				.getIdSnapshot().get(ID_PK_COLUMN) : null;
	}
}
