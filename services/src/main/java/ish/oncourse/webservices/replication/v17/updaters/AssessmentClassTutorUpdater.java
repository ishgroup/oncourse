/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v17.updaters;

import ish.oncourse.model.AssessmentClass;
import ish.oncourse.model.AssessmentClassTutor;
import ish.oncourse.model.Tutor;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v17.stubs.replication.AssessmentClassTutorStub;


public class AssessmentClassTutorUpdater extends AbstractWillowUpdater<AssessmentClassTutorStub, AssessmentClassTutor> {

	@Override
	protected void updateEntity(AssessmentClassTutorStub stub, AssessmentClassTutor entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setAssessmentClass(callback.updateRelationShip(stub.getAssessmentClassId(), AssessmentClass.class));
		entity.setTutor(callback.updateRelationShip(stub.getTutorId(), Tutor.class));
	}
}
