package ish.oncourse.model;

import ish.oncourse.model.auto._Voucher;
import ish.oncourse.utils.QueueableObjectUtils;

public class Voucher extends _Voucher implements Queueable {
	private static final long serialVersionUID = 8216738488416614101L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}
}
