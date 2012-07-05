package ish.oncourse.webservices.replication.v4.builders;

import ish.common.types.AttachmentInfoVisibility;
import ish.oncourse.model.BinaryInfo;
import ish.oncourse.webservices.v4.stubs.replication.BinaryInfoStub;

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
		//this implementation is correct for backward compatibility in v5 stubs commented line should be used
		stub.setWebVisible(AttachmentInfoVisibility.PUBLIC.equals(entity.getWebVisible()));
		//stub.setWebVisible(entity.getWebVisible().getDatabaseValue());
		return stub;
	}
	
}
