/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v16.builders;

import ish.oncourse.model.AssessmentClassTutor;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v16.stubs.replication.AssessmentClassTutorStub;

public class AssessmentClassTutorStubBuilder extends AbstractWillowStubBuilder<AssessmentClassTutor, AssessmentClassTutorStub> {
	@Override
	protected AssessmentClassTutorStub createFullStub(AssessmentClassTutor entity) {
		AssessmentClassTutorStub stub = new AssessmentClassTutorStub();
		stub.setModified(entity.getModified());
		stub.setAssessmentClassId(entity.getAssessmentClass().getId());
		stub.setTutorId(entity.getTutor().getId());
		return stub;

	}
}
