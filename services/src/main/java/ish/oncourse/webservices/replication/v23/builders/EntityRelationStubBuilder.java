package ish.oncourse.webservices.replication.v23.builders;

import ish.oncourse.model.EntityRelation;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v23.stubs.replication.EntityRelationStub;

public class EntityRelationStubBuilder extends AbstractWillowStubBuilder<EntityRelation, EntityRelationStub> {

	@Override
	protected EntityRelationStub createFullStub(EntityRelation entity) {
		EntityRelationStub stub = new EntityRelationStub();
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setFromEntityWillowId(entity.getFromEntityWillowId());
		stub.setFromEntityIdentifier(entity.getFromEntityIdentifier());
		stub.setToEntityWillowId(entity.getToEntityWillowId());
		stub.setToEntityIdentifier(entity.getToEntityIdentifier());
		stub.setRelationTypeId(entity.getRelationType() != null ? entity.getRelationType().getId() : null);
		return stub;
	}

}
