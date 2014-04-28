package ish.oncourse.model;

import ish.oncourse.model.auto._DocumentVersion;
import ish.oncourse.utils.QueueableObjectUtils;

public class DocumentVersion extends _DocumentVersion implements Queueable {

	@Override
	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
