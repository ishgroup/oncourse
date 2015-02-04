/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v9.updaters;

import ish.oncourse.model.Contact;
import ish.oncourse.model.CustomField;
import ish.oncourse.model.CustomFieldType;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v9.stubs.replication.CustomFieldStub;

public class CustomFieldUpdater extends AbstractWillowUpdater<CustomFieldStub, CustomField> {

	@Override
	protected void updateEntity(CustomFieldStub stub, CustomField entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		
		entity.setCustomFieldType(callback.updateRelationShip(stub.getCustomFieldTypeId(), CustomFieldType.class));
		entity.setRelatedObject(callback.updateRelationShip(stub.getForeignId(), Contact.class));
		entity.setValue(stub.getValue());
	}
}
