package ish.oncourse.model;

import ish.oncourse.model.auto._FieldConfigurationScheme;
import ish.oncourse.utils.QueueableObjectUtils;

public class FieldConfigurationScheme extends _FieldConfigurationScheme implements Queueable{

    private static final long serialVersionUID = 1L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
