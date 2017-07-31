/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v16.updaters;

import ish.oncourse.model.PriorLearning;
import ish.oncourse.model.Qualification;
import ish.oncourse.model.Student;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v16.stubs.replication.PriorLearningStub;
import org.apache.cayenne.Cayenne;

public class PriorLearningUpdater extends AbstractWillowUpdater<PriorLearningStub, PriorLearning> {
	@Override
	protected void updateEntity(PriorLearningStub stub, PriorLearning entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setNotes(stub.getNotes());
		entity.setExternalRef(stub.getExternalRef());
		entity.setOutcomeIdTrainingOrg(stub.getOutcomeIdTrainingOrg());
		entity.setTitle(stub.getTitle());
		if (stub.getQualificationId() != null) {
			entity.setQualification(Cayenne.objectForPK(entity.getObjectContext(), Qualification.class, stub.getQualificationId()));
		}
		entity.setStudent(callback.updateRelationShip(stub.getStudentId(), Student.class));
	}
}
