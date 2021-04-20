/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v23.builders;

import ish.oncourse.model.Assessment;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v23.stubs.replication.AssessmentStub;

public class AssessmentStubBuilder extends AbstractWillowStubBuilder<Assessment, AssessmentStub> {
	@Override
	protected AssessmentStub createFullStub(Assessment entity) {
		AssessmentStub stub = new AssessmentStub();
		stub.setModified(entity.getModified());
		stub.setCode(entity.getCode());
		stub.setName(entity.getName());
		stub.setIsActive(entity.getActive());
		stub.setDescription(entity.getDescription());
		if (entity.getGradingType() != null) {
			stub.setGradingTypeId(entity.getGradingType().getId());
		}
		return stub;
	}
}
