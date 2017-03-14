package ish.oncourse.model;

import ish.oncourse.model.auto._WaitingList;
import ish.oncourse.utils.QueueableObjectUtils;

public class WaitingList extends _WaitingList implements Queueable {
	private static final long serialVersionUID = 8659761513629004303L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
