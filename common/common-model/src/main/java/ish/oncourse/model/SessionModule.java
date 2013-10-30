package ish.oncourse.model;

import ish.oncourse.model.auto._SessionModule;
import ish.oncourse.utils.QueueableObjectUtils;

public class SessionModule extends _SessionModule implements Queueable {
	private static final long serialVersionUID = -6936312225691806816L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
