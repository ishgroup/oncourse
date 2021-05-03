/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import ish.oncourse.server.cayenne.CustomFieldType
import ish.oncourse.webservices.v23.stubs.replication.CustomFieldTypeStub

class CustomFieldTypeUpdater extends AbstractAngelUpdater<CustomFieldTypeStub, CustomFieldType> {

	@Override
	protected void updateEntity(CustomFieldTypeStub stub, CustomFieldType entity, RelationShipCallback callback) {
		entity.setCreatedOn(stub.getCreated())
		entity.setModifiedOn(stub.getModified())

		entity.setName(stub.getName())
		entity.setDefaultValue(stub.getDefaultValue())
		entity.setPattern(stub.getPattern())
		entity.setIsMandatory(stub.isMandatory())
		entity.setKey(stub.getKey())

		entity.setEntityIdentifier(stub.getEntityName())
	}
}
