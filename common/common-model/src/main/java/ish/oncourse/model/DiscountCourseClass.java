package ish.oncourse.model;

import ish.oncourse.model.auto._DiscountCourseClass;
import ish.oncourse.utils.QueueableObjectUtils;

public class DiscountCourseClass extends _DiscountCourseClass implements Queueable {
	private static final long serialVersionUID = 6222791641274072920L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
