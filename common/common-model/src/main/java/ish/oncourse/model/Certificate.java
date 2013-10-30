package ish.oncourse.model;

import ish.oncourse.model.auto._Certificate;
import ish.oncourse.utils.QueueableObjectUtils;

public class Certificate extends _Certificate implements Queueable {
	private static final long serialVersionUID = 5333455040759639988L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
