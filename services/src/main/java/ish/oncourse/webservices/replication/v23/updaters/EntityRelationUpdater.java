package ish.oncourse.webservices.replication.v23.updaters;

import ish.oncourse.model.*;
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
		entity.setFromEntityWillowId(callback.updateRelationShip(stub.getFromEntityAngelId(), getClassByName(fromType)).getId());
		String toType = stub.getToEntityIdentifier();
		entity.setToEntityIdentifier(toType);
		entity.setToEntityWillowId(callback.updateRelationShip(stub.getToEntityAngelId(), getClassByName(toType)).getId());
		entity.setRelationType(callback.updateRelationShip(stub.getRelationTypeId(), EntityRelationType.class));
	}

	private Class<? extends Queueable> getClassByName(String name) {
		try {
			return Class.forName(name).asSubclass(Queueable.class);
		} catch (ClassNotFoundException | ClassCastException e) {
			e.printStackTrace();
			return null;
		}
	}

}
