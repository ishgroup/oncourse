package ish.oncourse.model;

import ish.oncourse.model.auto._DiscussionThread;
import ish.oncourse.utils.QueueableObjectUtils;

public class DiscussionThread extends _DiscussionThread {
	private static final long serialVersionUID = 2844843019170228072L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}
}
