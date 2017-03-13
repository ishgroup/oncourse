package ish.oncourse.model;

import ish.oncourse.model.auto._Qualification;
import ish.oncourse.utils.QueueableObjectUtils;

import java.util.Date;

public class Qualification extends _Qualification {
	private static final long serialVersionUID = 5408403857605726093L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	/*
	 * @see ish.oncourse.model.auto._Qualification#onPreUpdate()
	 */
	@Override
	protected void onPreUpdate() {
		Date today = new Date();
		if (getCreated() == null) {
			setCreated(today);
		}
		setModified(today);
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
