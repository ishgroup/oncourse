package ish.oncourse.model;

import ish.oncourse.model.auto._TrainingPackage;
import ish.oncourse.utils.QueueableObjectUtils;

import java.util.Date;

public class TrainingPackage extends _TrainingPackage {
	private static final long serialVersionUID = -9132949626628478915L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	/*
	 * @see ish.oncourse.model.auto._TrainingPackage#onPreUpdate()
	 */
	@Override
	protected void onPreUpdate() {
		setModified(new Date());
	}

	/*
	 * @see ish.oncourse.model.auto._TrainingPackage#onPrePersist()
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
