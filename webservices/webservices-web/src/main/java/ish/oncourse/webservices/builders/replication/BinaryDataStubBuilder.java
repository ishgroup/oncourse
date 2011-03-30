package ish.oncourse.webservices.builders.replication;

import ish.oncourse.model.BinaryData;
import ish.oncourse.webservices.v4.stubs.replication.BinaryDataStub;

public class BinaryDataStubBuilder extends AbstractWillowStubBuilder<BinaryData, BinaryDataStub> {
	
	@Override
	protected BinaryDataStub createFullStub(BinaryData entity) {
		BinaryDataStub stub = new BinaryDataStub();
		
		stub.setBinaryInfoId(entity.getBinaryInfo().getId());
		stub.setContent(entity.getContent());
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setIsDeleted(entity.getIsDeleted());
		stub.setWillowId(entity.getId());
		stub.setAngelId(entity.getAngelId());
		
		return stub;
	}
}
