/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v16.updaters;

import ish.common.types.ContactDuplicateStatus;
import ish.common.types.TypesUtil;
import ish.oncourse.model.Contact;
import ish.oncourse.model.ContactDuplicate;
import ish.oncourse.model.SystemUser;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v16.stubs.replication.ContactDuplicateStub;

public class ContactDuplicateUpdater  extends AbstractWillowUpdater<ContactDuplicateStub, ContactDuplicate> {
	@Override
	protected void updateEntity(ContactDuplicateStub stub, ContactDuplicate entity, RelationShipCallback callback) {
		entity.setDescription(stub.getDescription());
		entity.setStatus(TypesUtil.getEnumForDatabaseValue(stub.getStatus(), ContactDuplicateStatus.class));
		if(stub.getCreatedBy() != null) {
			entity.setCreatedByUser(callback.updateRelationShip(stub.getCreatedBy(), SystemUser.class));
		}
		entity.setContactToUpdate(callback.updateRelationShip(stub.getContactToUpdateId(), Contact.class));
		entity.setContactToDeleteId(stub.getContactToDeleteWillowId());
		entity.setContactToDeleteAngelId(stub.getContactToDeleteAngelId());
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());

	}
}
