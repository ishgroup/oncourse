package ish.oncourse.model;

import ish.oncourse.model.auto._Message;
import ish.oncourse.utils.QueueableObjectUtils;

public class Message extends _Message implements Queueable {
	private static final long serialVersionUID = -874881400684150129L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}

}
