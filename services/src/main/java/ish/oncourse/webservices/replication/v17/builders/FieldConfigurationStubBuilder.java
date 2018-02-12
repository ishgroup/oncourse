/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v17.builders;

import ish.common.types.FieldConfigurationType;
import ish.oncourse.model.FieldConfiguration;
import ish.oncourse.model.SurveyFieldConfiguration;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v17.stubs.replication.FieldConfigurationStub;

public class FieldConfigurationStubBuilder extends AbstractWillowStubBuilder<FieldConfiguration, FieldConfigurationStub> {

	@Override
	protected FieldConfigurationStub createFullStub(FieldConfiguration entity) {
		FieldConfigurationStub stub = new FieldConfigurationStub();
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setName(entity.getName());
		stub.setType(entity.getType().getDatabaseValue());
		if (FieldConfigurationType.SURVEY.equals(entity.getType())) {
			stub.setDeliverySchedule(((SurveyFieldConfiguration)entity).getDeliverySchedule().getDatabaseValue());
		}
		
		return stub;
	}
}
