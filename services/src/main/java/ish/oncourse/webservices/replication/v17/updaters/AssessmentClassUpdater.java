/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v17.updaters;

import ish.oncourse.model.Assessment;
import ish.oncourse.model.AssessmentClass;
import ish.oncourse.model.CourseClass;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v17.stubs.replication.AssessmentClassStub;

public class AssessmentClassUpdater extends AbstractWillowUpdater<AssessmentClassStub, AssessmentClass> {
	
	@Override
	protected void updateEntity(AssessmentClassStub stub, AssessmentClass entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setReleaseDate(stub.getReleaseDate());
		entity.setDueDate(stub.getDueDate());
		entity.setAssessment(callback.updateRelationShip(stub.getAssessmentId(), Assessment.class));
		entity.setCourseClass(callback.updateRelationShip(stub.getCourseClassId(), CourseClass.class));
	}
}
