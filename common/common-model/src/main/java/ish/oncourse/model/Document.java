package ish.oncourse.model;

import ish.oncourse.model.auto._Document;
import ish.oncourse.utils.QueueableObjectUtils;

public class Document extends _Document implements Queueable {

	@Override
	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
