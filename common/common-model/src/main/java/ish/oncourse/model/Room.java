package ish.oncourse.model;

import ish.oncourse.model.auto._Room;
import ish.oncourse.utils.QueueableObjectUtils;

public class Room extends _Room implements Queueable {
	private static final long serialVersionUID = -9123155231053381087L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
