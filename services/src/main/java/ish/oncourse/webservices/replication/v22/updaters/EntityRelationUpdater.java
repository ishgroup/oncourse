package ish.oncourse.webservices.replication.v22.updaters;

import ish.oncourse.model.Course;
import ish.oncourse.model.EntityRelation;
import ish.oncourse.model.Product;
import ish.oncourse.model.Queueable;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v22.stubs.replication.EntityRelationStub;

import java.util.HashMap;
import java.util.Map;

public class EntityRelationUpdater extends AbstractWillowUpdater<EntityRelationStub, EntityRelation> {
	
	protected static final Map<String, Class<? extends Queueable>> ENTITY_CLASS_MAPPING = new HashMap<>();
	static {
		ENTITY_CLASS_MAPPING.put("Course", Course.class);
		ENTITY_CLASS_MAPPING.put("Product", Product.class);
	}

	@Override
	protected void updateEntity(EntityRelationStub stub, EntityRelation entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());

		String fromType = getStringValueFromNum(stub.getFromEntityIdentifier());
		entity.setFromEntityIdentifier(fromType);
		entity.setFromEntityWillowId(callback.updateRelationShip(stub.getFromEntityAngelId(), ENTITY_CLASS_MAPPING.get(fromType)).getId());

		String toType = getStringValueFromNum(stub.getToEntityIdentifier());
		entity.setToEntityIdentifier(toType);
		entity.setToEntityWillowId(callback.updateRelationShip(stub.getToEntityAngelId(), ENTITY_CLASS_MAPPING.get(toType)).getId());
	}

	private String getStringValueFromNum(Integer num) {
		if (num == 2) {
			return "Product";
		}
		return "Course";
	}

}
