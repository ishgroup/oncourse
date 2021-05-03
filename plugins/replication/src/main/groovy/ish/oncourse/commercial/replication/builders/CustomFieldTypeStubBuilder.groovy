/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.CustomFieldType
import ish.oncourse.webservices.v23.stubs.replication.CustomFieldTypeStub

class CustomFieldTypeStubBuilder extends AbstractAngelStubBuilder<CustomFieldType, CustomFieldTypeStub> {

	@Override
	protected CustomFieldTypeStub createFullStub(CustomFieldType entity) {
		def stub = new CustomFieldTypeStub()

		stub.setCreated(entity.getCreatedOn())
		stub.setModified(entity.getModifiedOn())

		stub.setName(entity.getName())
		stub.setDefaultValue(entity.getDefaultValue())
		stub.setPattern(entity.getPattern())
		stub.setMandatory(entity.getIsMandatory())
		stub.setKey(entity.getKey())

		stub.setEntityName(entity.getEntityIdentifier())
		stub.setDataType(entity.getDataType().getDatabaseValue())

		return stub
	}
}
