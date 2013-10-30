package ish.oncourse.model;

import ish.oncourse.model.auto._SessionTutor;
import ish.oncourse.utils.QueueableObjectUtils;

public class SessionTutor extends _SessionTutor implements Queueable {
	private static final long serialVersionUID = 3676423292868756757L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
