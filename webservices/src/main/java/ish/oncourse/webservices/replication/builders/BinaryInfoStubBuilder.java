package ish.oncourse.webservices.replication.builders;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.webservices.v4.stubs.replication.BinaryInfoStub;

public class BinaryInfoStubBuilder extends AbstractWillowStubBuilder<BinaryInfo, BinaryInfoStub> {
	
	@Override
	protected BinaryInfoStub createFullStub(BinaryInfo entity) {
		BinaryInfoStub stub = new BinaryInfoStub();
		
		stub.setBinaryDataId(entity.getBinaryData().getId());
		stub.setByteSize(entity.getByteSize());
		stub.setCreated(entity.getCreated());
		stub.setMimeType(entity.getMimeType());
		stub.setModified(entity.getModified());
		stub.setName(entity.getName());
		stub.setPixelHeight(entity.getPixelHeight());
		stub.setPixelWidth(entity.getPixelWidth());
		stub.setReferenceNumber(entity.getReferenceNumber());
		stub.setWebVisible(entity.getIsWebVisible());
		
		return stub;
	}
	
}
