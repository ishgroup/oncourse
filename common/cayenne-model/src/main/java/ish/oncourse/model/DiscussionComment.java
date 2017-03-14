package ish.oncourse.model;

import ish.oncourse.model.auto._DiscussionComment;
import ish.oncourse.utils.QueueableObjectUtils;

public class DiscussionComment extends _DiscussionComment {
	private static final long serialVersionUID = -9111047642217489022L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}
}
