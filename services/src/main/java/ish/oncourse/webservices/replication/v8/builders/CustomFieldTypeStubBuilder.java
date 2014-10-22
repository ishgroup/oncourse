/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v8.builders;

import ish.oncourse.model.CustomFieldType;
import ish.oncourse.webservices.replication.v4.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v8.stubs.replication.CustomFieldTypeStub;

public class CustomFieldTypeStubBuilder extends AbstractWillowStubBuilder<CustomFieldType, CustomFieldTypeStub> {

	@Override
	protected CustomFieldTypeStub createFullStub(CustomFieldType entity) {
		CustomFieldTypeStub stub = new CustomFieldTypeStub();
		
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		
		stub.setName(entity.getName());
		stub.setDefaultValue(entity.getDefaultValue());
		stub.setMandatory(entity.getIsMandatory());
		
		return stub;
	}
}
