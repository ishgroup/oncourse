package ish.oncourse.model;

import ish.oncourse.model.auto._ContactRelationType;
import ish.oncourse.utils.QueueableObjectUtils;

public class ContactRelationType extends _ContactRelationType implements Queueable {
	private static final long serialVersionUID = -7597843829557304320L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return false;
	}
}
