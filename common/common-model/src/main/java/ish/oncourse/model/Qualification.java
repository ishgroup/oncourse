package ish.oncourse.model;

import java.util.Date;

import ish.oncourse.model.auto._Qualification;
import ish.oncourse.utils.QueueableObjectUtils;

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
