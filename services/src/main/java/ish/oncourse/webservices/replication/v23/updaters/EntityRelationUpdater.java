package ish.oncourse.webservices.replication.v23.updaters;

import ish.oncourse.model.*;
import ish.oncourse.model.Module;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v23.stubs.replication.EntityRelationStub;

public class EntityRelationUpdater extends AbstractWillowUpdater<EntityRelationStub, EntityRelation> {

	@Override
	protected void updateEntity(EntityRelationStub stub, EntityRelation entity, RelationShipCallback callback) {
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
		String fromType = stub.getFromEntityIdentifier();
		entity.setFromEntityIdentifier(fromType);
		entity.setFromEntityWillowId(getEntityWillowId(stub.getFromEntityAngelId(), fromType, callback));
		String toType = stub.getToEntityIdentifier();
		entity.setToEntityIdentifier(toType);
		entity.setToEntityWillowId(getEntityWillowId(stub.getToEntityAngelId(), toType, callback));
		entity.setRelationType(callback.updateRelationShip(stub.getRelationTypeId(), EntityRelationType.class));
	}

	private Long getEntityWillowId(Long entityId, String entityName, RelationShipCallback callback) {
		if (!entityName.equals(Module.class.getSimpleName()) && !entityName.equals(Qualification.class.getSimpleName())) {
			return callback.updateRelationShip(entityId, getClassByName(entityName)).getId();
		}
		return entityId;
	}

	private Class<? extends Queueable> getClassByName(String name) {
		try {
			String path = EntityRelation.class.getPackage().getName();
			return Class.forName(path.concat(".").concat(name)).asSubclass(Queueable.class);
		} catch (ClassNotFoundException | ClassCastException e) {
			e.printStackTrace();
			return null;
		}
	}

}
