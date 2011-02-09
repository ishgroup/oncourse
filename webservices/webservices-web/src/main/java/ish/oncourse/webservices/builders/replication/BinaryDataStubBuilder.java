package ish.oncourse.webservices.builders.replication;

import ish.oncourse.model.BinaryData;
import ish.oncourse.model.BinaryInfo;
import ish.oncourse.model.QueuedKey;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.builders.IReplicationStubBuilder;
import ish.oncourse.webservices.v4.stubs.replication.BinaryDataStub;
import ish.oncourse.webservices.v4.stubs.replication.BinaryInfoStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;

import java.util.Map;

public class BinaryDataStubBuilder extends AbstractReplicationStubBuilder {

	public BinaryDataStubBuilder(Map<QueuedKey, QueuedRecord> queue, IReplicationStubBuilder next) {
		super(queue, next);
	}

	@Override
	public ReplicationStub createFullStub(QueuedRecord entity) {
		BinaryDataStub stub = new BinaryDataStub();

		BinaryData record = (BinaryData) findMatchingEntity(entity);

		stub.setAngelId(record.getAngelId());
		stub.setCollegeId(record.getCollegeId());
		stub.setContent(record.getContent());
		stub.setCreated(record.getCreated());
		stub.setIsDeleted(record.getIsDeleted());

		BinaryInfo info = record.getBinaryInfo();

		QueuedRecord infoRecord = queue.get(new QueuedKey(info.getId(), info.getObjectId().getEntityName()));

		if (infoRecord != null) {
			stub.setBinaryInfo(next.convert(infoRecord));
			queue.remove(infoRecord);
		} else {
			BinaryInfoStub infoStub = new BinaryInfoStub();
			infoStub.setWillowId(info.getId());
			stub.setBinaryInfo(infoStub);
		}

		return stub;
	}

	@Override
	public ReplicationStub createDeletedStub(QueuedRecord entity) {
		BinaryDataStub stub = new BinaryDataStub();
		stub.setState("DELETED");
		stub.setWillowId(entity.getEntityWillowId());
		return stub;
	}
}
