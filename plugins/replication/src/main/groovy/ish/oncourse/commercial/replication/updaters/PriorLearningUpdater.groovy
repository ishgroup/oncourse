/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import ish.oncourse.server.cayenne.PriorLearning
import ish.oncourse.server.cayenne.Student
import ish.oncourse.server.reference.ReferenceUtil
import ish.oncourse.webservices.v21.stubs.replication.PriorLearningStub

class PriorLearningUpdater extends AbstractAngelUpdater<PriorLearningStub, PriorLearning> {

	@Override
	protected void updateEntity(PriorLearningStub stub, PriorLearning entity, RelationShipCallback callback) {
		entity.setCreatedOn(stub.getCreated())
		entity.setModifiedOn(stub.getModified())
		entity.setExternalRef(stub.getExternalRef())
		entity.setTitle(stub.getTitle())
		entity.setNotes(stub.getNotes())
		entity.setOutcomeIdTrainingOrg(stub.getOutcomeIdTrainingOrg())
		entity.setStudent(callback.updateRelationShip(stub.getStudentId(), Student.class))
		if (stub.getQualificationId() != null) {
			entity.setQualification(ReferenceUtil.findQualificationByWillowId(entity.getObjectContext(), stub.getQualificationId()))
		}
	}
}
