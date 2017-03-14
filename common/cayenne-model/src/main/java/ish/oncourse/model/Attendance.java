package ish.oncourse.model;

import ish.oncourse.model.auto._Attendance;
import ish.oncourse.utils.QueueableObjectUtils;

public class Attendance extends _Attendance implements Queueable {
	private static final long serialVersionUID = 5699863428784294272L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
