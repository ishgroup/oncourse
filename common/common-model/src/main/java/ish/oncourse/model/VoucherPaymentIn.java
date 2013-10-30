package ish.oncourse.model;

import ish.oncourse.model.auto._VoucherPaymentIn;
import ish.oncourse.utils.QueueableObjectUtils;

public class VoucherPaymentIn extends _VoucherPaymentIn implements Queueable {
	private static final long serialVersionUID = -8979854047939163675L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
