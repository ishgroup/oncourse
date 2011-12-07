package ish.oncourse.model;

import ish.oncourse.model.auto._VoucherProduct;
import ish.oncourse.utils.QueueableObjectUtils;

public class VoucherProduct extends _VoucherProduct implements Queueable {
	private static final long serialVersionUID = 4859536151566879248L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}
}
