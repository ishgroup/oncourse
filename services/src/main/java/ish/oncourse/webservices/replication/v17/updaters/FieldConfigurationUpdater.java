/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v17.updaters;

import ish.common.types.DeliverySchedule;
import ish.common.types.FieldConfigurationType;
import ish.common.types.TypesUtil;
import ish.oncourse.model.FieldConfiguration;
import ish.oncourse.model.SurveyFieldConfiguration;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v17.stubs.replication.FieldConfigurationStub;

public class FieldConfigurationUpdater extends AbstractWillowUpdater<FieldConfigurationStub, FieldConfiguration> {
	@Override
	protected void updateEntity(FieldConfigurationStub stub, FieldConfiguration entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		entity.setName(stub.getName());
		if (FieldConfigurationType.SURVEY.getDatabaseValue().equals(stub.getType())) {
			DeliverySchedule value = TypesUtil.getEnumForDatabaseValue(stub.getDeliverySchedule(), DeliverySchedule.class);
			((SurveyFieldConfiguration) entity).setDeliverySchedule(value);
		}
	}
}
