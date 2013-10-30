package ish.oncourse.model;

import ish.oncourse.model.auto._ContactRelation;
import ish.oncourse.utils.QueueableObjectUtils;

public class ContactRelation extends _ContactRelation implements Queueable {
	private static final long serialVersionUID = -5540791410063253192L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return false;
	}
}
