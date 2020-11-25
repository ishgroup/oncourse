/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v23.builders;

import ish.oncourse.model.FieldConfigurationScheme;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v23.stubs.replication.FieldConfigurationSchemeStub;

public class FieldConfigurationSchemeStubBuilder extends AbstractWillowStubBuilder<FieldConfigurationScheme, FieldConfigurationSchemeStub> {

	@Override
	protected FieldConfigurationSchemeStub createFullStub(FieldConfigurationScheme entity) {
		FieldConfigurationSchemeStub stub = new FieldConfigurationSchemeStub();
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setName(entity.getName());
		stub.setCreatedBy(entity.getCreatedBy().getId());
		return stub;
	}
}
