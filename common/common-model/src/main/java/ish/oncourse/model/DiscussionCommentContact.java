package ish.oncourse.model;

import ish.oncourse.model.auto._DiscussionCommentContact;
import ish.oncourse.utils.QueueableObjectUtils;

public class DiscussionCommentContact extends _DiscussionCommentContact {
	private static final long serialVersionUID = 6933156202767534220L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}
}
