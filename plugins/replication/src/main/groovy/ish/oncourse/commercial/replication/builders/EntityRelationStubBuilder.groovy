/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.EntityRelation
import ish.oncourse.server.cayenne.Module
import ish.oncourse.server.cayenne.Qualification
import ish.oncourse.server.cayenne.Queueable
import ish.oncourse.server.cayenne.glue.CayenneDataObject
import ish.oncourse.webservices.v23.stubs.replication.EntityRelationStub
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById

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

		stub.setFromEntityIdentifier(entity.getFromEntityIdentifier())
		stub.setToEntityIdentifier(entity.getToEntityIdentifier())
		stub.setFromEntityAngelId(getWillowIdOfEntity(entity.getFromEntityAngelId(), entity.getFromEntityIdentifier(), entity.getContext()))
		stub.setToEntityAngelId(getWillowIdOfEntity(entity.getToEntityAngelId(), entity.getToEntityIdentifier(), entity.getContext()))

		stub.setRelationTypeId(entity.getRelationType().getId())

		return stub
	}

	private static Long getWillowIdOfEntity(Long entityId, String entityName, ObjectContext context) {
		if (Module.simpleName == entityName) {
			return SelectById.query(Module, entityId).selectOne(context).getWillowId()
		}
		if (Qualification.simpleName == entityName) {
			return SelectById.query(Qualification, entityId).selectOne(context).getWillowId()
		}
		return entityId
	}

}
