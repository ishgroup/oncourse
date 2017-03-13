package ish.oncourse.model;

import ish.oncourse.model.auto._Assessment;
import ish.oncourse.utils.QueueableObjectUtils;

public class Assessment extends _Assessment implements Queueable {

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
