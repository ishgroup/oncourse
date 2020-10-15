/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.ContactRelationType
import ish.oncourse.webservices.v21.stubs.replication.ContactRelationTypeStub

/**
 */
class ContactRelationTypeStubBuilder extends AbstractAngelStubBuilder<ContactRelationType, ContactRelationTypeStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected ContactRelationTypeStub createFullStub(final ContactRelationType entity) {
		final def stub = new ContactRelationTypeStub()
		stub.setCreated(entity.getCreatedOn())
		stub.setFromContactName(entity.getFromContactName())
		stub.setAngelId(entity.getId())
		stub.setModified(entity.getModifiedOn())
		stub.setToContactName(entity.getToContactName())
        /**
         * delegatedAccessToContact property for the stub was declared as Integer so we need the odd code.
         * We can not change the implementation because we need to support old colleges.
         * We can adjust it only in V8 replication
         */
        stub.setDelegatedAccessToContact(entity.getDelegatedAccessToContact() != null && entity.getDelegatedAccessToContact() ? 1:0)
		stub.setWillowId(entity.getWillowId())
		if (entity.getCreatedBy() != null) {
			stub.setCreatedBy(entity.getCreatedBy().getId())
		}
		return stub
	}
}
