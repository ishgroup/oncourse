package ish.oncourse.webservices.replication.v7.updaters;

import ish.common.types.EntityRelationType;
import ish.common.types.TypesUtil;
import ish.oncourse.model.Course;
import ish.oncourse.model.EntityRelation;
import ish.oncourse.model.Product;
import ish.oncourse.model.Queueable;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v7.stubs.replication.EntityRelationStub;

import java.util.HashMap;
import java.util.Map;

public class EntityRelationUpdater extends AbstractWillowUpdater<EntityRelationStub, EntityRelation> {
	
	protected static final Map<EntityRelationType, Class<? extends Queueable>> ENTITY_CLASS_MAPPING = new HashMap<>();
	static {
		ENTITY_CLASS_MAPPING.put(EntityRelationType.COURSE, Course.class);
		ENTITY_CLASS_MAPPING.put(EntityRelationType.PRODUCT, Product.class);
	}

	@Override
	protected void updateEntity(EntityRelationStub stub, EntityRelation entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		EntityRelationType fromType = TypesUtil.getEnumForDatabaseValue(stub.getFromEntityIdentifier(), EntityRelationType.class);
		entity.setFromEntityIdentifier(fromType);
		entity.setFromEntityWillowId(callback.updateRelationShip(stub.getFromEntityAngelId(), ENTITY_CLASS_MAPPING.get(fromType)).getId());
		EntityRelationType toType = TypesUtil.getEnumForDatabaseValue(stub.getFromEntityIdentifier(), EntityRelationType.class);
		entity.setToEntityIdentifier(toType);
		entity.setToEntityWillowId(callback.updateRelationShip(stub.getToEntityAngelId(), ENTITY_CLASS_MAPPING.get(toType)).getId());
	}

}
