package ish.oncourse.model;

import ish.oncourse.model.auto._BinaryInfoRelation;
import ish.oncourse.utils.QueueableObjectUtils;

public class BinaryInfoRelation extends _BinaryInfoRelation implements Queueable {
	private static final long serialVersionUID = -8842760929998183884L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
