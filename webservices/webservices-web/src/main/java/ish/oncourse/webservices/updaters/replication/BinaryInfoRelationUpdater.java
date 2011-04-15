package ish.oncourse.webservices.updaters.replication;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.BinaryInfoRelation;
import ish.oncourse.webservices.v4.stubs.replication.BinaryInfoRelationStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;

import java.util.List;

public class BinaryInfoRelationUpdater extends AbstractWillowUpdater<BinaryInfoRelationStub, BinaryInfoRelation> {

	@Override
	protected void updateEntity(BinaryInfoRelationStub stub, BinaryInfoRelation entity, List<ReplicatedRecord> result) {
		entity.setAngelId(stub.getAngelId());
		entity.setBinaryInfo(updateRelationShip(stub.getBinaryInfoId(), BinaryInfo.class, result));
		entity.setCollege(college);
		entity.setCreated(stub.getCreated());
		entity.setEntityAngelId(stub.getEntityAngelId());
		entity.setEntityIdentifier(stub.getEntityName());
		entity.setEntityWillowId(stub.getEntityWillowId());
		entity.setModified(stub.getModified());
	}
}
