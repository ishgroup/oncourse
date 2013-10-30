package ish.oncourse.model;

import ish.oncourse.model.auto._ConcessionType;
import ish.oncourse.utils.QueueableObjectUtils;

public class ConcessionType extends _ConcessionType implements Queueable {
	private static final long serialVersionUID = 366085604310079357L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
