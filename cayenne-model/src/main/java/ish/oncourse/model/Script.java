package ish.oncourse.model;

import ish.oncourse.model.auto._Script;
import ish.oncourse.utils.QueueableObjectUtils;

public class Script extends _Script implements Queueable {

	@Override
	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
