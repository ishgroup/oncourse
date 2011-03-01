package ish.oncourse.webservices.builders.replication;

import ish.oncourse.model.Queueable;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.model.QueuedKey;
import ish.oncourse.webservices.v4.stubs.replication.Action;
import ish.oncourse.webservices.v4.stubs.replication.DeletedStub;
import ish.oncourse.webservices.v4.stubs.replication.HollowStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;

import java.util.Map;

import org.apache.cayenne.DataObjectUtils;

public abstract class AbstractWillowStubBuilder<T extends Queueable, V extends ReplicationStub> implements IWillowStubBuilder {

	private IWillowStubBuilder next;
	private Map<QueuedKey, QueuedRecord> queue;

	public AbstractWillowStubBuilder(Map<QueuedKey, QueuedRecord> queue, IWillowStubBuilder next) {
		this.queue = queue;
		this.next = next;
	}

	private T findMatchingEntity(QueuedRecord entity) {
		@SuppressWarnings("unchecked")
		Class<T> entityClass = (Class<T>) entity.getObjectContext().getEntityResolver().getObjEntity(entity.getEntityIdentifier())
				.getClass();
		return DataObjectUtils.objectForPK(entity.getObjectContext(), entityClass, entity.getEntityWillowId());
	}

	protected ReplicationStub findRelatedStub(Queueable parent) {
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
			T entity = findMatchingEntity(record);
			ReplicationStub fullStub = createFullStub(entity);
			fullStub.setEntityIdentifier(record.getEntityIdentifier());
			fullStub.setAction(Action.valueOf(record.getAction().name()));
			return fullStub;
		case DELETE:
			DeletedStub deletedStub = new DeletedStub();
			deletedStub.setEntityIdentifier(record.getEntityIdentifier());
			deletedStub.setWillowId(record.getEntityWillowId());
			deletedStub.setAction(Action.valueOf(record.getAction().name()));
			return deletedStub;
		default:
			throw new IllegalArgumentException("QueuedRecord with null action is not allowed.");
		}
	}

	protected abstract V createFullStub(T entity);
}
