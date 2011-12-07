package ish.oncourse.model;

import ish.oncourse.model.auto._InvoiceLineDiscount;
import ish.oncourse.utils.QueueableObjectUtils;

public class InvoiceLineDiscount extends _InvoiceLineDiscount implements Queueable {
	private static final long serialVersionUID = -1535339144583077217L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

}
