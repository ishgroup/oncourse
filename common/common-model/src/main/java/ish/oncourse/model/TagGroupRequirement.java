package ish.oncourse.model;

import ish.oncourse.model.auto._TagGroupRequirement;
import ish.oncourse.utils.QueueableObjectUtils;

public class TagGroupRequirement extends _TagGroupRequirement implements Queueable {
	private static final long serialVersionUID = 5008022984929327344L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
