package ish.oncourse.webservices.replication.updaters;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.webservices.v4.stubs.replication.BinaryInfoStub;

public class BinaryInfoUpdater extends AbstractWillowUpdater<BinaryInfoStub, BinaryInfo> {

	@Override
	protected void updateEntity(BinaryInfoStub stub, BinaryInfo entity, RelationShipCallback callback) {
		entity.setByteSize(stub.getByteSize());
		entity.setCreated(stub.getCreated());
		entity.setIsWebVisible(stub.isWebVisible());
		entity.setMimeType(stub.getMimeType());
		entity.setModified(stub.getModified());
		entity.setName(stub.getName());
		if (stub.isWebVisible() && stub.getName().contains(BinaryInfo.UNSUPPORTED_NAME_CHARACTER)) {
			LOG.error(String.format("Unsupported characters exist in web visible binary info name: %s with id: %s angelid: %s", stub.getName(), 
				stub.getWillowId(), stub.getAngelId()));
		}
		entity.setPixelHeight(stub.getPixelHeight());
		entity.setPixelWidth(stub.getPixelWidth());
	}
}
