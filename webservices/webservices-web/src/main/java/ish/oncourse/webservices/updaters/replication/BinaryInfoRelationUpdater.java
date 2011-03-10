package ish.oncourse.webservices.updaters.replication;

import java.util.List;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.BinaryInfoRelation;
import ish.oncourse.model.College;
import ish.oncourse.webservices.services.replication.IWillowQueueService;
import ish.oncourse.webservices.v4.stubs.replication.BinaryInfoRelationStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicatedRecord;

public class BinaryInfoRelationUpdater extends AbstractWillowUpdater<BinaryInfoRelationStub, BinaryInfoRelation>{
	
	public BinaryInfoRelationUpdater(College college, IWillowQueueService queueService, @SuppressWarnings("rawtypes") IWillowUpdater next) {
		super(college, queueService, next);
	}

	@Override
	protected void updateEntity(BinaryInfoRelationStub stub, BinaryInfoRelation entity, List<ReplicatedRecord> relationStubs) {
		entity.setAngelId(stub.getAngelId());
		entity.setBinaryInfo((BinaryInfo) updateRelatedEntity(entity.getObjectContext(), stub.getBinaryInfo(), relationStubs));
		entity.setCollege(getCollege(entity.getObjectContext()));
		entity.setCreated(stub.getCreated());
		entity.setEntityAngelId(stub.getEntityAngelId());
		entity.setEntityIdentifier(stub.getEntityName());
		entity.setEntityWillowId(stub.getEntityWillowId());
		entity.setModified(stub.getModified());
	}
}
