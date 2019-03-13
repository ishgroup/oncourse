/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v20.updaters;

import ish.oncourse.model.Field;
import ish.oncourse.model.FieldConfiguration;
import ish.oncourse.model.FieldHeading;
import ish.oncourse.utils.FieldConfigurationUtil;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v20.stubs.replication.FieldStub;

public class FieldUpdater  extends AbstractWillowUpdater<FieldStub, Field> {
	@Override
	protected void updateEntity(FieldStub stub, Field entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setName(stub.getName());
		entity.setDescription(stub.getDescription());
		
		Class<? extends FieldConfiguration> fcClass = FieldConfigurationUtil.getClassByType(stub.getConfigurationType());
		entity.setFieldConfiguration(callback.updateRelationShip(stub.getFieldConfigurationId(), fcClass));
		
		entity.setFieldHeading(callback.updateRelationShip(stub.getFieldHeadingId(), FieldHeading.class));
		entity.setProperty(stub.getProperty());
		entity.setDefaultValue(stub.getDefaultValue());
		entity.setOrder(stub.getOrder());
		entity.setMandatory(stub.isMandatory());
	}
}
