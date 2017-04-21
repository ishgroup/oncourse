/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v15.updaters;

import ish.oncourse.model.FieldConfiguration;
import ish.oncourse.model.FieldHeading;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v15.stubs.replication.FieldHeadingStub;

public class FieldHeadingUpdater extends AbstractWillowUpdater<FieldHeadingStub, FieldHeading> {
	@Override
	protected void updateEntity(FieldHeadingStub stub, FieldHeading entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setName(stub.getName());
		entity.setDescription(stub.getDescription());
		entity.setFieldConfiguration(callback.updateRelationShip(stub.getFieldConfigurationId(), FieldConfiguration.class));
	}
}
