package ish.oncourse.model;

import ish.oncourse.model.auto._DiscountMembershipRelationType;
import ish.oncourse.utils.QueueableObjectUtils;

public class DiscountMembershipRelationType extends _DiscountMembershipRelationType implements Queueable {
	private static final long serialVersionUID = -2150468051746562115L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return false;
	}
}
