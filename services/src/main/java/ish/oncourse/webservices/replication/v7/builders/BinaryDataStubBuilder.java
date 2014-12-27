package ish.oncourse.webservices.replication.v7.builders;

import ish.oncourse.model.BinaryData;
import ish.oncourse.webservices.replication.builders.AbstractWillowStubBuilder;
import ish.oncourse.webservices.v7.stubs.replication.BinaryDataStub;

public class BinaryDataStubBuilder extends AbstractWillowStubBuilder<BinaryData, BinaryDataStub> {

	@Override
	protected BinaryDataStub createFullStub(BinaryData entity) {
		BinaryDataStub stub = new BinaryDataStub();
		stub.setBinaryInfoId(entity.getBinaryInfo().getId());
		stub.setContent(entity.getContent());
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		return stub;
	}
}
