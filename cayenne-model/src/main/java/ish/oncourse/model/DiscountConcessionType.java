package ish.oncourse.model;

import ish.oncourse.model.auto._DiscountConcessionType;
import ish.oncourse.utils.QueueableObjectUtils;

public class DiscountConcessionType extends _DiscountConcessionType implements Queueable {
	private static final long serialVersionUID = 4380729467308593404L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
