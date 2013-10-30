package ish.oncourse.model;

import ish.oncourse.model.auto._CorporatePassCourseClass;
import ish.oncourse.utils.QueueableObjectUtils;

public class CorporatePassCourseClass extends _CorporatePassCourseClass implements Queueable {
	
	private static final long serialVersionUID = -6850742947565803648L;

	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
