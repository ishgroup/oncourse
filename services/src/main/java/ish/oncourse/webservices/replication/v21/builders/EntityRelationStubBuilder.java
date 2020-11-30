package ish.oncourse.webservices.replication.v21.builders;

import ish.oncourse.model.EntityRelation;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v21.stubs.replication.EntityRelationStub;

public class EntityRelationStubBuilder extends AbstractWillowStubBuilder<EntityRelation, EntityRelationStub> {

	@Override
	protected EntityRelationStub createFullStub(EntityRelation entity) {
		EntityRelationStub stub = new EntityRelationStub();
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setFromEntityWillowId(entity.getFromEntityWillowId());
		stub.setFromEntityIdentifier(getStringValueFromNum(entity.getFromEntityIdentifier()));
		stub.setToEntityWillowId(entity.getToEntityWillowId());
		stub.setToEntityIdentifier(getStringValueFromNum(entity.getToEntityIdentifier()));
		return stub;
	}

	private int getStringValueFromNum(String value) {
		if (value.equals("Product")) {
			return 2;
		}
		return 1;
	}

}
