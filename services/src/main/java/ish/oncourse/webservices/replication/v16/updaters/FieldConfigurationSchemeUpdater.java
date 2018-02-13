/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v16.updaters;

import ish.oncourse.model.EnrolmentFieldConfiguration;
import ish.oncourse.model.FieldConfigurationScheme;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v16.stubs.replication.FieldConfigurationSchemeStub;

public class FieldConfigurationSchemeUpdater extends AbstractWillowUpdater<FieldConfigurationSchemeStub, FieldConfigurationScheme> {
	@Override
	protected void updateEntity(FieldConfigurationSchemeStub stub, FieldConfigurationScheme entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setName(stub.getName());
		entity.setApplicationFieldConfiguration(callback.updateRelationShip(stub.getApplicationFieldConfigurationId(), EnrolmentFieldConfiguration.class));
		entity.setEnrolFieldConfiguration(callback.updateRelationShip(stub.getEnrolFieldConfigurationId(), EnrolmentFieldConfiguration.class));
		entity.setWaitingListFieldConfiguration(callback.updateRelationShip(stub.getWaitingListFieldConfigurationId(), EnrolmentFieldConfiguration.class));
	}
}
