package ish.oncourse.model;

import ish.oncourse.cayenne.AssessmentClassInterface;
import ish.oncourse.model.auto._AssessmentClass;
import ish.oncourse.utils.QueueableObjectUtils;

public class AssessmentClass extends _AssessmentClass implements Queueable, AssessmentClassInterface {

    private static final long serialVersionUID = 1L;

	@Override
	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}
}
