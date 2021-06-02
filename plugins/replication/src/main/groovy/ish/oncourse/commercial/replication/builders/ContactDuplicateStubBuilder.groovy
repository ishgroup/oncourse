/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import groovy.transform.CompileStatic
import ish.oncourse.server.cayenne.ContactDuplicate
import ish.oncourse.webservices.v23.stubs.replication.ContactDuplicateStub

@CompileStatic
class ContactDuplicateStubBuilder extends AbstractAngelStubBuilder<ContactDuplicate, ContactDuplicateStub> {
	@Override
	protected ContactDuplicateStub createFullStub(ContactDuplicate entity) {
		def stub = new ContactDuplicateStub()
		stub.setCreated(entity.getCreatedOn())
		stub.setModified(entity.getModifiedOn())
		stub.setContactToDeleteAngelId(entity.getContactToDeleteId())
		stub.setContactToDeleteWillowId(entity.getContactToDeleteWillowId())
		stub.setContactToUpdateId(entity.getContactToUpdate().getId())
		stub.setDescription(entity.getDescription())
		stub.setStatus(entity.getStatus().getDatabaseValue())
		if (entity.getCreatedByUser() != null) {
			stub.setCreatedBy(entity.getCreatedByUser().getId())
		}
		return stub
	}
}
