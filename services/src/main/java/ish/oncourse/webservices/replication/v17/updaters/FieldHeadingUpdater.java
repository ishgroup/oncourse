/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v17.updaters;

import ish.oncourse.model.FieldConfiguration;
import ish.oncourse.model.FieldHeading;
import ish.oncourse.utils.FieldConfigurationUtil;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v17.stubs.replication.FieldHeadingStub;

public class FieldHeadingUpdater extends AbstractWillowUpdater<FieldHeadingStub, FieldHeading> {
	@Override
	protected void updateEntity(FieldHeadingStub stub, FieldHeading entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setName(stub.getName());
		entity.setDescription(stub.getDescription());
		
		Class<? extends FieldConfiguration> fcClass = FieldConfigurationUtil.getClassByType(stub.getConfigurationType());
		entity.setFieldConfiguration(callback.updateRelationShip(stub.getFieldConfigurationId(), fcClass));
		
		entity.setOrder(stub.getOrder());
	}
}
