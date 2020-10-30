/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.ContactRelation
import ish.oncourse.webservices.v22.stubs.replication.ContactRelationStub

/**
 */
class ContactRelationStubBuilder extends AbstractAngelStubBuilder<ContactRelation, ContactRelationStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected ContactRelationStub createFullStub(final ContactRelation entity) {
		final def stub = new ContactRelationStub()
		stub.setCreated(entity.getCreatedOn())
		stub.setFromContactId(entity.getFromContact().getId())
		stub.setModified(entity.getModifiedOn())
		stub.setToContactId(entity.getToContact().getId())
		stub.setTypeId(entity.getRelationType().getId())
		stub.setAngelId(entity.getId())
		stub.setWillowId(entity.getWillowId())
		return stub
	}
}
