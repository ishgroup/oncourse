package ish.oncourse.model;

import ish.oncourse.cayenne.FieldInterface;
import ish.oncourse.model.auto._Field;
import ish.oncourse.utils.QueueableObjectUtils;

public class Field extends _Field implements Queueable, FieldInterface{

    private static final long serialVersionUID = 1L;
	
	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
