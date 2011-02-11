package ish.oncourse.webservices.builders.replication;

import ish.oncourse.model.BinaryData;
import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.QueuedKey;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.builders.IReplicationStubBuilder;
import ish.oncourse.webservices.v4.stubs.replication.BinaryDataStub;
import ish.oncourse.webservices.v4.stubs.replication.BinaryInfoStub;
import ish.oncourse.webservices.v4.stubs.replication.StubState;

import java.util.Map;

public class BinaryDataStubBuilder extends AbstractReplicationStubBuilder<BinaryData, BinaryDataStub> {

	public BinaryDataStubBuilder(Map<QueuedKey, QueuedRecord> queue, IReplicationStubBuilder next) {
		super(queue, next);
	}

	@Override
	public BinaryDataStub createEmptyStub() {
		return new BinaryDataStub();
	}

	@Override
	public void fillStubValues(BinaryData record, BinaryDataStub stub) {
		stub.setAngelId(record.getAngelId());
		stub.setCollegeId(record.getCollegeId());
		stub.setContent(record.getContent());
		stub.setCreated(record.getCreated());
		stub.setIsDeleted(record.getIsDeleted());
		
		//Fill relationships
		BinaryInfo info = record.getBinaryInfo();
		QueuedKey key = new QueuedKey(info.getId(), info.getObjectId().getEntityName());
		QueuedRecord infoRecord = queue.get(key);

		if (infoRecord != null) {
			stub.setBinaryInfo((BinaryInfoStub) next.convert(infoRecord));
			queue.remove(key);
		} else {
			BinaryInfoStub infoStub = new BinaryInfoStub();
			infoStub.setWillowId(info.getId());
			infoStub.setState(StubState.HOLLOW);
			stub.setBinaryInfo(infoStub);
		}
	}
}
