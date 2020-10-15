/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.EntityRelation
import ish.oncourse.webservices.v21.stubs.replication.EntityRelationStub

/**
 */
class EntityRelationStubBuilder extends AbstractAngelStubBuilder<EntityRelation, EntityRelationStub> {

	/**
	 * @see AbstractAngelStubBuilder#createFullStub(ish.oncourse.server.cayenne.Queueable)
	 */
	@Override
	protected EntityRelationStub createFullStub(EntityRelation entity) {
		def stub = new EntityRelationStub()

		stub.setCreated(entity.getCreatedOn())
		stub.setModified(entity.getModifiedOn())

		stub.setFromEntityIdentifier(entity.getFromEntityIdentifier().getDatabaseValue())
		stub.setToEntityIdentifier(entity.getToEntityIdentifier().getDatabaseValue())
		stub.setFromEntityAngelId(entity.getFromEntityAngelId())
		stub.setToEntityAngelId(entity.getToEntityAngelId())

		return stub
	}

}
