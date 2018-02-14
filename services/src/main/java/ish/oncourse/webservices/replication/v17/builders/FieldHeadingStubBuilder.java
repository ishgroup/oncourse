/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v17.builders;


import ish.oncourse.model.FieldHeading;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v17.stubs.replication.FieldHeadingStub;


public class FieldHeadingStubBuilder extends AbstractWillowStubBuilder<FieldHeading, FieldHeadingStub> {
	@Override
	protected FieldHeadingStub createFullStub(FieldHeading entity) {
		FieldHeadingStub stub = new FieldHeadingStub();
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setName(entity.getName());
		stub.setDescription(entity.getDescription());
		stub.setFieldConfigurationId(entity.getFieldConfiguration().getId());
		stub.setConfigurationType(entity.getFieldConfiguration().getType().getDatabaseValue());
		stub.setOrder(entity.getOrder());
		return stub;
	}
}
