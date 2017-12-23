/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v17.builders;

import ish.oncourse.model.PriorLearning;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v17.stubs.replication.PriorLearningStub;

public class PriorLearningStubBuilder extends AbstractWillowStubBuilder<PriorLearning, PriorLearningStub> {
	
	@Override
	protected PriorLearningStub createFullStub(PriorLearning entity) {
		PriorLearningStub stub = new PriorLearningStub();
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setNotes(entity.getNotes());
		stub.setExternalRef(entity.getExternalRef());
		stub.setOutcomeIdTrainingOrg(entity.getOutcomeIdTrainingOrg());
		stub.setTitle(entity.getTitle());		
		if (entity.getQualification() != null) {
			stub.setQualificationId(entity.getQualification().getId());
		}
		stub.setStudentId(entity.getStudent().getId());
		
		return stub;
	}
}
