/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v23.updaters;

import ish.oncourse.model.Assessment;
import ish.oncourse.model.GradingType;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v23.stubs.replication.AssessmentStub;

public class AssessmentUpdater extends AbstractWillowUpdater<AssessmentStub, Assessment> {
	@Override
	protected void updateEntity(AssessmentStub stub, Assessment entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setActive(stub.isIsActive());
		entity.setCode(stub.getCode());
		entity.setName(stub.getName());
		entity.setDescription(stub.getDescription());
		entity.setGradingType(callback.updateRelationShip(stub.getGradingTypeId(), GradingType.class));
	}
}
