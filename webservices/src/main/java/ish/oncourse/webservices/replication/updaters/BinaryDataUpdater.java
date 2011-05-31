package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.BinaryData;
import ish.oncourse.model.BinaryInfo;
import ish.oncourse.webservices.v4.stubs.replication.BinaryDataStub;

public class BinaryDataUpdater extends AbstractWillowUpdater<BinaryDataStub, BinaryData> {

	@Override
	protected void updateEntity(BinaryDataStub stub, BinaryData entity, RelationShipCallback callback) {
		entity.setContent(stub.getContent());
		entity.setBinaryInfo(callback.updateRelationShip(stub.getBinaryInfoId(), BinaryInfo.class));
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
	}
}
