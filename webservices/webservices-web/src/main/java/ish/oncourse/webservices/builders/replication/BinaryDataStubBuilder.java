package ish.oncourse.webservices.builders.replication;

import ish.oncourse.model.BinaryData;
import ish.oncourse.model.QueuedKey;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.v4.stubs.replication.BinaryDataStub;

import java.util.Map;

public class BinaryDataStubBuilder extends AbstractWillowStubBuilder<BinaryData, BinaryDataStub> {
	
	public BinaryDataStubBuilder(Map<QueuedKey, QueuedRecord> queue, IWillowStubBuilder next) {
		super(queue, next);
	}
	
	@Override
	protected BinaryDataStub createFullStub(BinaryData entity) {
		BinaryDataStub stub = new BinaryDataStub();
		
		stub.setBinaryInfo(findRelatedStub(entity.getBinaryInfo()));
		stub.setCollegeId(entity.getCollegeId());
		stub.setContent(entity.getContent());
		stub.setCreated(entity.getCreated());
		stub.setModified(entity.getModified());
		stub.setIsDeleted(entity.getIsDeleted());
		stub.setWillowId(entity.getId());
		stub.setAngelId(entity.getAngelId());
		
		return stub;
	}
}
