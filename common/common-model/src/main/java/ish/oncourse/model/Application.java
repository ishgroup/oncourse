package ish.oncourse.model;

import ish.common.types.ConfirmationStatus;
import ish.oncourse.model.auto._Application;
import ish.oncourse.utils.QueueableObjectUtils;

import java.util.Date;

public class Application extends _Application implements Queueable {

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
			setConfirmationStatus(ConfirmationStatus.NOT_SENT);
		}
	}
}
