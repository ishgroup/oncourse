package ish.oncourse.webservices.replication.v15.updaters;

import ish.common.types.AttachmentInfoVisibility;
import ish.common.types.TypesUtil;
import ish.oncourse.model.BinaryInfo;
import ish.oncourse.webservices.replication.updaters.AbstractWillowUpdater;
import ish.oncourse.webservices.replication.updaters.RelationShipCallback;
import ish.oncourse.webservices.v15.stubs.replication.BinaryInfoStub;

public class BinaryInfoUpdater extends AbstractWillowUpdater<BinaryInfoStub, BinaryInfo> {

	@Override
	protected void updateEntity(BinaryInfoStub stub, BinaryInfo entity, RelationShipCallback callback) {
		entity.setByteSize(stub.getByteSize());
		entity.setCreated(stub.getCreated());
		entity.setWebVisible(TypesUtil.getEnumForDatabaseValue(stub.getWebVisible(), AttachmentInfoVisibility.class));
		entity.setMimeType(stub.getMimeType());
		entity.setModified(stub.getModified());
		entity.setName(stub.getName());
		entity.setPixelHeight(stub.getPixelHeight());
		entity.setPixelWidth(stub.getPixelWidth());
		entity.setFileUUID(stub.getFileUUID());
	}
}
