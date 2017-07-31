/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v16.builders;

import ish.oncourse.model.FieldConfigurationScheme;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v16.stubs.replication.FieldConfigurationSchemeStub;

public class FieldConfigurationSchemeStubBuilder extends AbstractWillowStubBuilder<FieldConfigurationScheme, FieldConfigurationSchemeStub> {

	@Override
	protected FieldConfigurationSchemeStub createFullStub(FieldConfigurationScheme entity) {
		FieldConfigurationSchemeStub stub = new FieldConfigurationSchemeStub();
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setApplicationFieldConfigurationId(entity.getApplicationFieldConfiguration().getId());
		stub.setEnrolFieldConfigurationId(entity.getEnrolFieldConfiguration().getId());
		stub.setWaitingListFieldConfigurationId(entity.getWaitingListFieldConfiguration().getId());
		stub.setName(entity.getName());
		return stub;
	}
}
