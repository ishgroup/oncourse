package ish.oncourse.model;

import ish.oncourse.model.auto._CorporatePassProduct;
import ish.oncourse.utils.QueueableObjectUtils;

public class CorporatePassProduct extends _CorporatePassProduct implements Queueable{

    private static final long serialVersionUID = 1L;

	@Override
	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
