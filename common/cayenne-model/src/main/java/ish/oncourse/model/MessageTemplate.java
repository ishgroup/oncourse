package ish.oncourse.model;

import ish.oncourse.model.auto._MessageTemplate;
import ish.oncourse.utils.QueueableObjectUtils;

public class MessageTemplate extends _MessageTemplate implements Queueable {
	private static final long serialVersionUID = 6281149453288962472L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
