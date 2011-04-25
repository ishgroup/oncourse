package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.BinaryInfoRelation;
import ish.oncourse.webservices.v4.stubs.replication.BinaryInfoRelationStub;

public class BinaryInfoRelationUpdater extends AbstractWillowUpdater<BinaryInfoRelationStub, BinaryInfoRelation> {

	@Override
	protected void updateEntity(BinaryInfoRelationStub stub, BinaryInfoRelation entity, RelationShipCallback callback) {
		entity.setAngelId(stub.getAngelId());
		entity.setBinaryInfo(callback.updateRelationShip(stub.getBinaryInfoId(), BinaryInfo.class));
		entity.setCreated(stub.getCreated());
		entity.setEntityAngelId(stub.getEntityAngelId());
		entity.setEntityIdentifier(stub.getEntityName());
		entity.setEntityWillowId(stub.getEntityWillowId());
		entity.setModified(stub.getModified());
	}
}
