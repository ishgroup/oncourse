package ish.oncourse.model;

import ish.oncourse.model.auto._CorporatePassDiscount;
import ish.oncourse.utils.QueueableObjectUtils;

public class CorporatePassDiscount extends _CorporatePassDiscount implements Queueable {

    private static final long serialVersionUID = 1L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
