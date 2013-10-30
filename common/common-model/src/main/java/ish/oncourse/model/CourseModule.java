package ish.oncourse.model;

import ish.oncourse.model.auto._CourseModule;
import ish.oncourse.utils.QueueableObjectUtils;

public class CourseModule extends _CourseModule implements Queueable {
	private static final long serialVersionUID = 7414459937670592474L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
