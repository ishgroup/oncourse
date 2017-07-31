/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v16.builders;

import ish.oncourse.model.FieldConfiguration;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v16.stubs.replication.FieldConfigurationStub;

public class FieldConfigurationStubBuilder extends AbstractWillowStubBuilder<FieldConfiguration, FieldConfigurationStub> {

	@Override
	protected FieldConfigurationStub createFullStub(FieldConfiguration entity) {
		FieldConfigurationStub stub = new FieldConfigurationStub();
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setName(entity.getName());
		return stub;
	}
}
