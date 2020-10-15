/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.updaters

import ish.common.types.EntityRelationType
import ish.common.types.TypesUtil
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.EntityRelation
import ish.oncourse.server.cayenne.Product
import ish.oncourse.server.cayenne.Queueable
import ish.oncourse.webservices.v21.stubs.replication.EntityRelationStub

import java.util.EnumMap
import java.util.Map

/**
 */
class EntityRelationUpdater extends AbstractAngelUpdater<EntityRelationStub, EntityRelation> {

	private static final Map<EntityRelationType, Class<? extends Queueable>> ENTITY_CLASS_MAPPING = new EnumMap<>(EntityRelationType.class)
	static {
		ENTITY_CLASS_MAPPING.put(EntityRelationType.COURSE, Course.class)
		ENTITY_CLASS_MAPPING.put(EntityRelationType.PRODUCT, Product.class)
	}

	/**
	 * @see AbstractAngelUpdater#updateEntity(ish.oncourse.webservices.util.GenericReplicationStub,
	 *      Queueable, RelationShipCallback)
	 */
	@Override
	protected void updateEntity(EntityRelationStub stub, EntityRelation entity, RelationShipCallback callback) {
		entity.setCreatedOn(stub.getCreated())
		entity.setModifiedOn(stub.getModified())

		def fromType = TypesUtil.getEnumForDatabaseValue(stub.getFromEntityIdentifier(), EntityRelationType.class)
		entity.setFromEntityIdentifier(fromType)
		entity.setFromEntityAngelId(callback.updateRelationShip(stub.getFromEntityWillowId(), ENTITY_CLASS_MAPPING.get(fromType)).getId())

		def toType = TypesUtil.getEnumForDatabaseValue(stub.getToEntityIdentifier(), EntityRelationType.class)
		entity.setToEntityIdentifier(toType)
		entity.setToEntityAngelId(callback.updateRelationShip(stub.getToEntityWillowId(), ENTITY_CLASS_MAPPING.get(toType)).getId())
	}

}
