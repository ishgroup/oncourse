/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.CustomField
import ish.oncourse.webservices.v21.stubs.replication.CustomFieldStub

class CustomFieldStubBuilder extends AbstractAngelStubBuilder<CustomField, CustomFieldStub> {

	@Override
	protected CustomFieldStub createFullStub(CustomField entity) {
		def stub = new CustomFieldStub()

		stub.setCreated(entity.getCreatedOn())
		stub.setModified(entity.getModifiedOn())

		stub.setCustomFieldTypeId(entity.getCustomFieldType().getId())
		stub.setForeignId(entity.getRelatedObject().getId())
		stub.setValue(entity.getValue())
		stub.setEntityName(entity.getEntityIdentifier())

		return stub
	}
}
