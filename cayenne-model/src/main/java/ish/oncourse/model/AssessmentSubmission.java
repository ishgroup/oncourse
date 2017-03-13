package ish.oncourse.model;

import ish.oncourse.model.auto._AssessmentSubmission;
import ish.oncourse.utils.QueueableObjectUtils;

public class AssessmentSubmission extends _AssessmentSubmission implements Queueable {

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
