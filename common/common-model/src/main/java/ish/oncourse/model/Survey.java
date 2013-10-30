package ish.oncourse.model;

import ish.oncourse.model.auto._Survey;
import ish.oncourse.utils.QueueableObjectUtils;

public class Survey extends _Survey implements Queueable {
	private static final long serialVersionUID = -4483770644382704684L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
