package ish.oncourse.model;

import ish.common.types.FieldConfigurationType;
import ish.oncourse.model.auto._FieldConfiguration;
import ish.oncourse.utils.QueueableObjectUtils;

public abstract class FieldConfiguration extends _FieldConfiguration implements Queueable{

    private static final long serialVersionUID = 1L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}

	public abstract FieldConfigurationType getType();
}
