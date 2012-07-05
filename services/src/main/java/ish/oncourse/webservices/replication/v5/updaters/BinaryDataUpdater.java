package ish.oncourse.webservices.replication.v5.updaters;

import ish.oncourse.model.BinaryData;
import ish.oncourse.model.BinaryInfo;
import ish.oncourse.webservices.replication.v4.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.v4.updaters.RelationShipCallback;
import ish.oncourse.webservices.v5.stubs.replication.BinaryDataStub;

public class BinaryDataUpdater extends AbstractWillowUpdater<BinaryDataStub, BinaryData> {

	@Override
	protected void updateEntity(BinaryDataStub stub, BinaryData entity, RelationShipCallback callback) {
		entity.setContent(stub.getContent());
		entity.setBinaryInfo(callback.updateRelationShip(stub.getBinaryInfoId(), BinaryInfo.class));
		entity.setCreated(stub.getCreated());
		entity.setModified(stub.getModified());
	}
}
