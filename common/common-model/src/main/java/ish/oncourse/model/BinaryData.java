package ish.oncourse.model;

import ish.oncourse.model.auto._BinaryData;
import ish.oncourse.utils.QueueableObjectUtils;

@Deprecated //the entity will be deleted in future
public class BinaryData extends _BinaryData implements Queueable {
	private static final long serialVersionUID = -4698878662757171690L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
