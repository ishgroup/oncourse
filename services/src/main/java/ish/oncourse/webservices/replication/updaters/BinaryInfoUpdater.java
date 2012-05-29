package ish.oncourse.webservices.replication.updaters;

import ish.common.types.AttachmentInfoVisibility;
//import ish.common.types.TypesUtil;
import ish.oncourse.model.BinaryInfo;
import ish.oncourse.webservices.v4.stubs.replication.BinaryInfoStub;

public class BinaryInfoUpdater extends AbstractWillowUpdater<BinaryInfoStub, BinaryInfo> {

	@Override
	protected void updateEntity(BinaryInfoStub stub, BinaryInfo entity, RelationShipCallback callback) {
		entity.setByteSize(stub.getByteSize());
		entity.setCreated(stub.getCreated());
		AttachmentInfoVisibility webVisibility = stub.isWebVisible() ? AttachmentInfoVisibility.PUBLIC : AttachmentInfoVisibility.PRIVATE;
		entity.setWebVisible(webVisibility);
		//this implementation is correct for backward compatibility in v5 stubs commented line should be used
		//entity.setWebVisible(TypesUtil.getEnumForDatabaseValue(stub.getWebVisible(), AttachmentInfoVisibility.class));
		entity.setMimeType(stub.getMimeType());
		entity.setModified(stub.getModified());
		entity.setName(stub.getName());
		entity.setPixelHeight(stub.getPixelHeight());
		entity.setPixelWidth(stub.getPixelWidth());
	}
}
