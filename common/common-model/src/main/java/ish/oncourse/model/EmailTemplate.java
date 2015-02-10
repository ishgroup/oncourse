package ish.oncourse.model;

import ish.oncourse.model.auto._EmailTemplate;
import ish.oncourse.utils.QueueableObjectUtils;

public class EmailTemplate extends _EmailTemplate implements Queueable {

	@Override
	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
