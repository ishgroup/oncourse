package ish.oncourse.model;

import ish.common.payable.EnrolmentInterface;
import ish.oncourse.model.auto._AssessmentSubmission;
import ish.oncourse.utils.QueueableObjectUtils;
import ish.oncourse.cayenne.AssessmentSubmissionInterface;

import java.time.LocalDate;
import java.util.Date;

public class AssessmentSubmission extends _AssessmentSubmission implements AssessmentSubmissionInterface, Queueable {

    private static final long serialVersionUID = 1L;

	@Override
	public Long getId() {
		return QueueableObjectUtils.getId(this);
	}

	@Override
	public boolean isAsyncReplicationAllowed() {
		return true;
	}

	@Override
	public Date getSubmittedDate() {
		return getSubmittedOn();
	}
	
}
