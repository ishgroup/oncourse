package ish.oncourse.model;

import ish.oncourse.model.auto._CorporatePass;
import ish.oncourse.utils.QueueableObjectUtils;

public class CorporatePass extends _CorporatePass implements Queueable {
	
	private static final long serialVersionUID = 3256263619229243861L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
