package ish.oncourse.model;

import java.util.Date;

import ish.oncourse.model.auto._Qualification;

public class Qualification extends _Qualification {

	public Long getId() {
		return (getObjectId() != null && !getObjectId().isTemporary()) ? (Long) getObjectId().getIdSnapshot().get(ID_PK_COLUMN) : null;
	}

	/*
	 * @see ish.oncourse.model.auto._Qualification#onPreUpdate()
	 */
	@Override
	protected void onPreUpdate() {
		setModified(new Date());
	}

	/*
	 * @see ish.oncourse.model.auto._Qualification#onPrePersist()
	 */
	@Override
	protected void onPrePersist() {
		Date today = new Date();

		if (getCreated() == null) {
			setCreated(today);
		}

		if (getModified() == null) {
			setModified(today);
		}
	}
}
