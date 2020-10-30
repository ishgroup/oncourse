/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.PriorLearning
import ish.oncourse.webservices.v22.stubs.replication.PriorLearningStub

class PriorLearningStubBuilder extends AbstractAngelStubBuilder<PriorLearning, PriorLearningStub> {

	@Override
	protected PriorLearningStub createFullStub(PriorLearning entity) {
		def stub = new PriorLearningStub()
		stub.setCreated(entity.getCreatedOn())
		stub.setModified(entity.getModifiedOn())
		stub.setExternalRef(entity.getExternalRef())
		stub.setTitle(entity.getTitle())
		stub.setNotes(entity.getNotes())
		stub.setOutcomeIdTrainingOrg(entity.getOutcomeIdTrainingOrg())
		stub.setStudentId(entity.getStudent().getId())
		if (entity.getQualification() != null) {
			stub.setQualificationId(entity.getQualification().getWillowId())
		}
		return stub
	}
}
