package ish.oncourse.webservices.replication.builders;

import ish.oncourse.model.Queueable;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.v4.stubs.replication.DeletedStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;

public abstract class AbstractWillowStubBuilder<T extends Queueable, V extends ReplicationStub> implements IWillowStubBuilder {

	public ReplicationStub convert(QueuedRecord queuedRecord) {
		
		@SuppressWarnings("unchecked")
		T entity = (T) queuedRecord.getLinkedRecord();
		
		switch (queuedRecord.getAction()) {
		case CREATE:
		case UPDATE:
			ReplicationStub fullStub = createFullStub(entity);
			fullStub.setEntityIdentifier(queuedRecord.getEntityIdentifier());
			fullStub.setWillowId(queuedRecord.getEntityWillowId());
			return fullStub;
		case DELETE:
			DeletedStub deletedStub = new DeletedStub();
			deletedStub.setEntityIdentifier(queuedRecord.getEntityIdentifier());
			deletedStub.setWillowId(queuedRecord.getEntityWillowId());
			return deletedStub;
		default:
			throw new IllegalArgumentException("QueuedRecord with null action is not allowed.");
		}
	}

	protected abstract V createFullStub(T entity);
}
