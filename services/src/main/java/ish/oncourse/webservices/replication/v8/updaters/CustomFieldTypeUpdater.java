/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v8.updaters;

import ish.oncourse.model.CustomFieldType;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v8.stubs.replication.CustomFieldTypeStub;

public class CustomFieldTypeUpdater extends AbstractWillowUpdater<CustomFieldTypeStub, CustomFieldType> {

	@Override
	protected void updateEntity(CustomFieldTypeStub stub, CustomFieldType entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		
		entity.setName(stub.getName());
		entity.setDefaultValue(stub.getDefaultValue());
		entity.setIsMandatory(stub.isMandatory());
	}
}
