package ish.oncourse.model;

import ish.oncourse.model.auto._CustomField;
import ish.oncourse.utils.QueueableObjectUtils;

public class CustomField extends _CustomField implements Queueable {

	@Override
	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
