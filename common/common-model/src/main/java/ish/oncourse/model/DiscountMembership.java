package ish.oncourse.model;

import ish.oncourse.model.auto._DiscountMembership;
import ish.oncourse.utils.QueueableObjectUtils;

public class DiscountMembership extends _DiscountMembership implements Queueable {
	private static final long serialVersionUID = -7909506094084503237L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return false;
	}
}
