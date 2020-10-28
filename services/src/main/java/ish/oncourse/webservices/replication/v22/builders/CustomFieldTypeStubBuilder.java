/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v22.builders;

import ish.oncourse.model.CustomFieldType;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v22.stubs.replication.CustomFieldTypeStub;

public class CustomFieldTypeStubBuilder extends AbstractWillowStubBuilder<CustomFieldType, CustomFieldTypeStub> {

	@Override
	protected CustomFieldTypeStub createFullStub(CustomFieldType entity) {
		CustomFieldTypeStub stub = new CustomFieldTypeStub();
		
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		
		stub.setName(entity.getName());
		stub.setDefaultValue(entity.getDefaultValue());
		stub.setMandatory(entity.getIsMandatory());
		stub.setKey(entity.getKey());
		stub.setEntityName(entity.getEntityName());
		if (entity.getDataType() != null) {
			stub.setDataType(entity.getDataType().getDatabaseValue());
		}
		return stub;
	}
}
