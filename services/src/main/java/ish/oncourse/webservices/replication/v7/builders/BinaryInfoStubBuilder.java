package ish.oncourse.webservices.replication.v7.builders;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.webservices.replication.v4.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v7.stubs.replication.BinaryInfoStub;

public class BinaryInfoStubBuilder extends AbstractWillowStubBuilder<BinaryInfo, BinaryInfoStub> {
	
	@Override
	protected BinaryInfoStub createFullStub(BinaryInfo entity) {
		BinaryInfoStub stub = new BinaryInfoStub();		
		stub.setByteSize(entity.getByteSize());
		stub.setCreated(entity.getCreated());
		stub.setMimeType(entity.getMimeType());
		stub.setModified(entity.getModified());
		stub.setName(entity.getName());
		stub.setPixelHeight(entity.getPixelHeight());
		stub.setPixelWidth(entity.getPixelWidth());
		stub.setReferenceNumber(entity.getReferenceNumber());
		stub.setWebVisible(entity.getWebVisible().getDatabaseValue());
		stub.setFileUUID(entity.getFileUUID());
		return stub;
	}
	
}
