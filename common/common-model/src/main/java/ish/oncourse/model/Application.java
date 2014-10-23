package ish.oncourse.model;

import ish.oncourse.model.auto._Application;
import ish.oncourse.utils.QueueableObjectUtils;

public class Application extends _Application implements Queueable {

	@Override
	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
