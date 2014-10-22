package ish.oncourse.webservices.replication.v8.builders;

import ish.oncourse.model.EntityRelation;
import ish.oncourse.webservices.replication.v4.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v8.stubs.replication.EntityRelationStub;

public class EntityRelationStubBuilder extends AbstractWillowStubBuilder<EntityRelation, EntityRelationStub> {

	@Override
	protected EntityRelationStub createFullStub(EntityRelation entity) {
		EntityRelationStub stub = new EntityRelationStub();
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setFromEntityWillowId(entity.getFromEntityWillowId());
		stub.setFromEntityIdentifier(entity.getFromEntityIdentifier().getDatabaseValue());
		stub.setToEntityWillowId(entity.getToEntityWillowId());
		stub.setToEntityIdentifier(entity.getToEntityIdentifier().getDatabaseValue());
		return stub;
	}

}
