/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v16.builders;

import ish.oncourse.model.Assessment;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v16.stubs.replication.AssessmentStub;

public class AssessmentStubBuilder extends AbstractWillowStubBuilder<Assessment, AssessmentStub> {
	@Override
	protected AssessmentStub createFullStub(Assessment entity) {
		AssessmentStub stub = new AssessmentStub();
		stub.setModified(entity.getModified());
		stub.setCode(entity.getCode());
		stub.setName(entity.getName());
		stub.setIsActive(entity.getActive());
		stub.setDescription(entity.getDescription());
		return stub;
	}
}
