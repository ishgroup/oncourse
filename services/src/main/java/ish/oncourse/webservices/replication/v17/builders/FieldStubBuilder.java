/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v17.builders;

import ish.oncourse.model.Field;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v17.stubs.replication.FieldStub;

public class FieldStubBuilder extends AbstractWillowStubBuilder<Field, FieldStub> {
	@Override
	protected FieldStub createFullStub(Field entity) {
		FieldStub stub = new FieldStub();
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setName(entity.getName());
		stub.setDescription(entity.getDescription());
		stub.setFieldConfigurationId(entity.getFieldConfiguration().getId());
		stub.setConfigurationType(entity.getFieldConfiguration().getType().getDatabaseValue());
		if (entity.getFieldHeading() != null) {
			stub.setFieldHeadingId(entity.getFieldHeading().getId());
		}
		stub.setMandatory(entity.getMandatory());
		stub.setOrder(entity.getOrder());
		stub.setProperty(entity.getProperty());
		stub.setDefaultValue(entity.getDefaultValue());
		return stub;
	}
}
