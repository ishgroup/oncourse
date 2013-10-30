package ish.oncourse.model;

import ish.oncourse.model.auto._Outcome;
import ish.oncourse.utils.QueueableObjectUtils;

public class Outcome extends _Outcome implements Queueable {
	private static final long serialVersionUID = 6238039938398442623L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
