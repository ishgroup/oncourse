package ish.oncourse.model;

import ish.oncourse.model.auto._CustomFieldType;
import ish.oncourse.utils.QueueableObjectUtils;

public class CustomFieldType extends _CustomFieldType implements Queueable {

	@Override
	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
