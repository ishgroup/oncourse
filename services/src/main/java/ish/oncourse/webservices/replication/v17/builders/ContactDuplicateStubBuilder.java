/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.webservices.replication.v17.builders;

import ish.oncourse.model.ContactDuplicate;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v17.stubs.replication.ContactDuplicateStub;

public class ContactDuplicateStubBuilder extends AbstractWillowStubBuilder<ContactDuplicate, ContactDuplicateStub> {
	@Override
	protected ContactDuplicateStub createFullStub(ContactDuplicate entity) {
		ContactDuplicateStub stub = new ContactDuplicateStub();
		stub.setCreatedBy(entity.getCreatedByUser().getId());
		stub.setDescription(entity.getDescription());
		stub.setStatus(entity.getStatus().getDatabaseValue());
		stub.setContactToUpdateId(entity.getContactToUpdate().getId());
		stub.setContactToDeleteWillowId(entity.getContactToDeleteId());
		stub.setContactToDeleteAngelId(entity.getContactToDeleteAngelId());
		stub.setModified(entity.getModified());
		return stub;
	}
}
