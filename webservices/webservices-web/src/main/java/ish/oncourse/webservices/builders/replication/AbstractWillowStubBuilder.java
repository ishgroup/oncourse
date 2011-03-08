package ish.oncourse.webservices.builders.replication;

import ish.oncourse.model.Queueable;
import ish.oncourse.model.QueuedKey;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.services.replication.IWillowQueueService;
import ish.oncourse.webservices.v4.stubs.replication.DeletedStub;
import ish.oncourse.webservices.v4.stubs.replication.HollowStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;

import java.util.Map;

public abstract class AbstractWillowStubBuilder<T extends Queueable, V extends ReplicationStub> implements IWillowStubBuilder {

	private IWillowStubBuilder next;
	
	private Map<QueuedKey, QueuedRecord> queue;

	private IWillowQueueService queueService;
	
	public AbstractWillowStubBuilder(Map<QueuedKey, QueuedRecord> queue, IWillowQueueService queueService, IWillowStubBuilder next) {
		this.queue = queue;
		this.queueService = queueService;
		this.next = next;
	}

	protected ReplicationStub findRelationshipStub(Queueable parent) {
		QueuedKey key = new QueuedKey(parent.getId(), parent.getObjectId().getEntityName());
		QueuedRecord bRecord = queue.get(key);

		if (bRecord != null) {
			queue.remove(key);
			return next.convert(bRecord);
		} else {
			HollowStub hollowStub = new HollowStub();
			hollowStub.setEntityIdentifier(parent.getObjectId().getEntityName());
			hollowStub.setWillowId(parent.getId());
			return hollowStub;
		}
	}

	public ReplicationStub convert(QueuedRecord record) {
		switch (record.getAction()) {
		case CREATE:
		case UPDATE:
			@SuppressWarnings("unchecked")
			T entity = (T) queueService.findRelatedEntity(record.getEntityIdentifier(), record.getEntityWillowId());
			ReplicationStub fullStub = createFullStub(entity);
			fullStub.setEntityIdentifier(record.getEntityIdentifier());
			return fullStub;
		case DELETE:
			DeletedStub deletedStub = new DeletedStub();
			deletedStub.setEntityIdentifier(record.getEntityIdentifier());
			deletedStub.setWillowId(record.getEntityWillowId());
			return deletedStub;
		default:
			throw new IllegalArgumentException("QueuedRecord with null action is not allowed.");
		}
	}

	protected abstract V createFullStub(T entity);
}
