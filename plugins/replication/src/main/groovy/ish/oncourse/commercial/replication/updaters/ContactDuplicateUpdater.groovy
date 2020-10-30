/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters


import ish.common.types.ContactDuplicateStatus
import ish.common.types.TypesUtil
import ish.oncourse.server.cayenne.Contact
import ish.oncourse.server.cayenne.ContactDuplicate
import ish.oncourse.server.cayenne.SystemUser
import ish.oncourse.webservices.v22.stubs.replication.ContactDuplicateStub

class ContactDuplicateUpdater extends AbstractAngelUpdater<ContactDuplicateStub, ContactDuplicate> {

	@Override
	protected void updateEntity(ContactDuplicateStub stub, ContactDuplicate entity, RelationShipCallback callback) {
		entity.setCreatedOn(stub.getCreated())
		entity.setModifiedOn(stub.getModified())
		entity.setStatus(TypesUtil.getEnumForDatabaseValue(stub.getStatus(), ContactDuplicateStatus.class))
		entity.setContactToDeleteId(stub.getContactToDeleteAngelId())
		entity.setContactToDeleteWillowId(stub.getContactToDeleteWillowId())
		entity.setContactToUpdate(callback.updateRelationShip(stub.getContactToUpdateId(), Contact.class))
		if (stub.getCreatedBy() != null) {
			entity.setCreatedByUser(callback.updateRelationShip(stub.getCreatedBy(), SystemUser.class))
		}
		entity.setDescription(stub.getDescription())
	}
}
