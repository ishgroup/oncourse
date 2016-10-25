package ish.oncourse.model;

import ish.oncourse.model.auto._AssessmentClassTutor;
import ish.oncourse.utils.QueueableObjectUtils;

public class AssessmentClassTutor extends _AssessmentClassTutor implements Queueable {

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
