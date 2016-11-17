package ish.oncourse.model;

import ish.oncourse.model.auto._FieldHeading;
import ish.oncourse.utils.QueueableObjectUtils;

public class FieldHeading extends _FieldHeading implements Queueable{

    private static final long serialVersionUID = 1L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
