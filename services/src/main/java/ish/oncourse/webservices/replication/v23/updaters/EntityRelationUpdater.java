package ish.oncourse.webservices.replication.v23.updaters;

import ish.common.types.EntityRelationIdentifier;
import ish.common.types.TypesUtil;
import ish.oncourse.model.*;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v23.stubs.replication.EntityRelationStub;

import java.util.HashMap;
import java.util.Map;

public class EntityRelationUpdater extends AbstractWillowUpdater<EntityRelationStub, EntityRelation> {
	
	protected static final Map<EntityRelationIdentifier, Class<? extends Queueable>> ENTITY_CLASS_MAPPING = new HashMap<>();
	static {
		ENTITY_CLASS_MAPPING.put(EntityRelationIdentifier.COURSE, Course.class);
		ENTITY_CLASS_MAPPING.put(EntityRelationIdentifier.PRODUCT, Product.class);
	}

	@Override
	protected void updateEntity(EntityRelationStub stub, EntityRelation entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		EntityRelationIdentifier fromType = TypesUtil.getEnumForDatabaseValue(stub.getFromEntityIdentifier(), EntityRelationIdentifier.class);
		entity.setFromEntityIdentifier(fromType);
		entity.setFromEntityWillowId(callback.updateRelationShip(stub.getFromEntityAngelId(), ENTITY_CLASS_MAPPING.get(fromType)).getId());
		EntityRelationIdentifier toType = TypesUtil.getEnumForDatabaseValue(stub.getToEntityIdentifier(), EntityRelationIdentifier.class);
		entity.setToEntityIdentifier(toType);
		entity.setToEntityWillowId(callback.updateRelationShip(stub.getToEntityAngelId(), ENTITY_CLASS_MAPPING.get(toType)).getId());
		entity.setRelationType(callback.updateRelationShip(stub.getRelationTypeId(), EntityRelationType.class));
	}

}
