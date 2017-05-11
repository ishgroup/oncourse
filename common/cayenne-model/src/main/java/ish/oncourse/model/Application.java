package ish.oncourse.model;

import ish.common.types.ConfirmationStatus;
import ish.oncourse.cayenne.IExpandable;
import ish.oncourse.model.auto._Application;
import ish.oncourse.utils.QueueableObjectUtils;

public class Application extends _Application implements Queueable, IExpandable {

	@Override
	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}

	@Override
	protected void onPostAdd() {
		if (getConfirmationStatus() == null) {
			setConfirmationStatus(ConfirmationStatus.DO_NOT_SEND);
		}
	}
}
