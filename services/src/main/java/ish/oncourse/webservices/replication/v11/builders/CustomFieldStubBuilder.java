/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v11.builders;

import ish.oncourse.model.CustomField;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v11.stubs.replication.CustomFieldStub;

public class CustomFieldStubBuilder extends AbstractWillowStubBuilder<CustomField, CustomFieldStub> {

	@Override
	protected CustomFieldStub createFullStub(CustomField entity) {
		CustomFieldStub stub = new CustomFieldStub();
		
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		
		stub.setCustomFieldTypeId(entity.getCustomFieldType().getId());
		stub.setForeignId(entity.getRelatedObject().getId());
		stub.setValue(entity.getValue());

		stub.setEntityIdentifier(CustomField.class.getSimpleName());
		
		return stub;
	}
}
