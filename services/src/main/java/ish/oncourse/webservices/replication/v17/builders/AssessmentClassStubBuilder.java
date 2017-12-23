/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v17.builders;

import ish.oncourse.model.AssessmentClass;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v17.stubs.replication.AssessmentClassStub;

public class AssessmentClassStubBuilder extends AbstractWillowStubBuilder<AssessmentClass, AssessmentClassStub> {

	@Override
	protected AssessmentClassStub createFullStub(AssessmentClass entity) {
		AssessmentClassStub stub = new AssessmentClassStub();
		stub.setModified(entity.getModified());
		stub.setAssessmentId(entity.getAssessment().getId());
		stub.setCourseClassId(entity.getCourseClass().getId());
		stub.setReleaseDate(entity.getReleaseDate());
		stub.setDueDate(entity.getDueDate());
		return stub;
	}
}
