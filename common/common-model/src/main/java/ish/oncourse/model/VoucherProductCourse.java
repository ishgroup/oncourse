package ish.oncourse.model;

import ish.oncourse.model.auto._VoucherProductCourse;
import ish.oncourse.utils.QueueableObjectUtils;

public class VoucherProductCourse extends _VoucherProductCourse implements Queueable {
	private static final long serialVersionUID = -1170834938475844971L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
