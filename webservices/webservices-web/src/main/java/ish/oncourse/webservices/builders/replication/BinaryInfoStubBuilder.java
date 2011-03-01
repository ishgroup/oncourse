package ish.oncourse.webservices.builders.replication;

import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.QueuedKey;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.v4.stubs.replication.BinaryInfoStub;

import java.util.Map;

public class BinaryInfoStubBuilder extends AbstractWillowStubBuilder<BinaryInfo, BinaryInfoStub> {
	
	public BinaryInfoStubBuilder(Map<QueuedKey, QueuedRecord> queue, IWillowStubBuilder next) {
		super(queue, next);
	}
	
	@Override
	protected BinaryInfoStub createFullStub(BinaryInfo entity) {
		BinaryInfoStub stub = new BinaryInfoStub();
		
		stub.setAngelId(entity.getAngelId());
		stub.setBinaryData(findRelatedStub(entity.getBinaryData()));
		stub.setByteSize(entity.getByteSize());
		stub.setCreated(entity.getCreated());
		stub.setMimeType(entity.getMimeType());
		stub.setModified(entity.getModified());
		stub.setName(entity.getName());
		stub.setPixelHeight(entity.getPixelHeight());
		stub.setPixelWidth(entity.getPixelWidth());
		stub.setReferenceNumber(entity.getReferenceNumber());
		stub.setWebVisible(entity.getIsWebVisible());
		stub.setWillowId(entity.getId());
		
		return stub;
	}
	
}
