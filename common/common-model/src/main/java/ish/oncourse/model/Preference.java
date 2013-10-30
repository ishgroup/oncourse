package ish.oncourse.model;

import ish.oncourse.model.auto._Preference;
import ish.oncourse.utils.QueueableObjectUtils;

public class Preference extends _Preference implements Queueable {
	private static final long serialVersionUID = 8309390847931508840L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
