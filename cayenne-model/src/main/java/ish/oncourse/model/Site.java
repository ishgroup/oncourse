package ish.oncourse.model;

import ish.oncourse.model.auto._Site;
import ish.oncourse.utils.QueueableObjectUtils;

public class Site extends _Site implements Queueable {
	private static final long serialVersionUID = -8489853837060919229L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	public boolean isHasCoordinates() {
		return getLatitude() != null && getLongitude() != null;
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
