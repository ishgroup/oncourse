/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.updaters

import ish.common.types.EntityRelationIdentifier
import ish.common.types.TypesUtil
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.EntityRelation
import ish.oncourse.server.cayenne.Product
import ish.oncourse.server.cayenne.Queueable
import ish.oncourse.webservices.v22.stubs.replication.EntityRelationStub

/**
 */
class EntityRelationUpdater extends AbstractAngelUpdater<EntityRelationStub, EntityRelation> {

	private static final Map<EntityRelationIdentifier, Class<? extends Queueable>> ENTITY_CLASS_MAPPING = new EnumMap<>(EntityRelationIdentifier.class)
	static {
		ENTITY_CLASS_MAPPING.put(EntityRelationIdentifier.COURSE, Course.class)
		ENTITY_CLASS_MAPPING.put(EntityRelationIdentifier.PRODUCT, Product.class)
	}

	/**
	 * @see AbstractAngelUpdater#updateEntity(ish.oncourse.webservices.util.GenericReplicationStub,
	 *      Queueable, RelationShipCallback)
	 */
	@Override
	protected void updateEntity(EntityRelationStub stub, EntityRelation entity, RelationShipCallback callback) {
		entity.setCreatedOn(stub.getCreated())
		entity.setModifiedOn(stub.getModified())

		EntityRelationIdentifier fromType = TypesUtil.getEnumForDatabaseValue(stub.getFromEntityIdentifier(), EntityRelationIdentifier.class)
		entity.setFromEntityIdentifier(fromType)
		entity.setFromEntityAngelId(callback.updateRelationShip(stub.getFromEntityWillowId(), ENTITY_CLASS_MAPPING.get(fromType)).getId())

		EntityRelationIdentifier toType = TypesUtil.getEnumForDatabaseValue(stub.getToEntityIdentifier(), EntityRelationIdentifier.class)
		entity.setToEntityIdentifier(toType)
		entity.setToEntityAngelId(callback.updateRelationShip(stub.getToEntityWillowId(), ENTITY_CLASS_MAPPING.get(toType)).getId())
	}

}
