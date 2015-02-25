package ish.oncourse.model;

import ish.oncourse.model.auto._InvoiceDueDate;
import ish.oncourse.utils.QueueableObjectUtils;

public class InvoiceDueDate extends _InvoiceDueDate implements Queueable{

	@Override
	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
