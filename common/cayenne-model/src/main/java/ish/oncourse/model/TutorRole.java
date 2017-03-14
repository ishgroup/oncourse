package ish.oncourse.model;

import ish.oncourse.model.auto._TutorRole;
import ish.oncourse.utils.QueueableObjectUtils;

public class TutorRole extends _TutorRole implements Queueable {
	private static final long serialVersionUID = -1757744129692597909L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
