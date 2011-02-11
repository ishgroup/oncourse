package ish.oncourse.webservices.builders.replication;

import java.util.Map;

import ish.oncourse.model.BinaryData;
import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.QueuedKey;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.builders.IReplicationStubBuilder;
import ish.oncourse.webservices.v4.stubs.replication.BinaryDataStub;
import ish.oncourse.webservices.v4.stubs.replication.BinaryInfoStub;
import ish.oncourse.webservices.v4.stubs.replication.StubState;

public class BinaryInfoStubBuilder extends AbstractReplicationStubBuilder<BinaryInfo, BinaryInfoStub> {

	public BinaryInfoStubBuilder(Map<QueuedKey, QueuedRecord> queue, IReplicationStubBuilder next) {
		super(queue, next);
	}
	
	@Override
	public BinaryInfoStub createEmptyStub() {
		return new BinaryInfoStub();
	}

	@Override
	public void fillStubValues(BinaryInfo record, BinaryInfoStub stub) {
		stub.setByteSize(record.getByteSize());
		stub.setCreated(record.getCreated());
		stub.setIsWebVisible(record.getIsWebVisible());
		stub.setMimeType(record.getMimeType());
		stub.setModified(record.getModified());
		stub.setName(record.getName());
		stub.setPixelHeight(record.getPixelHeight());
		stub.setPixelWidth(record.getPixelWidth());
		stub.setReferenceNumber(record.getReferenceNumber());
		stub.setWillowId(record.getId());
		
		//Fill relationships
		BinaryData bData = record.getBinaryData();
		QueuedKey key = new QueuedKey(bData.getId(), bData.getObjectId().getEntityName());
		QueuedRecord bRecord = queue.get(key);
		
		if (bRecord != null) {
			stub.setBinaryData((BinaryDataStub) next.convert(bRecord));
			queue.remove(key);
		}
		else {
			BinaryDataStub bDataStub = new BinaryDataStub();
			bDataStub.setState(StubState.HOLLOW);
			bDataStub.setWillowId(bData.getId());
			stub.setBinaryData(bDataStub);
		}
	}
}
