/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v23.builders;

import ish.oncourse.model.AssessmentSubmission;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v23.stubs.replication.AssessmentSubmissionStub;

public class AssessmentSubmissionStubBuilder extends AbstractWillowStubBuilder<AssessmentSubmission, AssessmentSubmissionStub>{
	@Override
	protected AssessmentSubmissionStub createFullStub(AssessmentSubmission entity) {
		AssessmentSubmissionStub stub = new AssessmentSubmissionStub();
		stub.setModified(entity.getModified());
		stub.setEnrolmentId(entity.getEnrolment().getId());
		stub.setAssessmentClassId(entity.getAssessmentClass().getId());
		stub.setSubmittedById(entity.getSubmittedBy().getId());
		stub.setSubmittedOn(entity.getSubmittedOn());
		stub.setMarkedOn(entity.getMarkedOn());
		return stub;
	}
}
