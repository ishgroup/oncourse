package ish.oncourse.webservices.builders.replication;

import ish.oncourse.model.Queueable;
import ish.oncourse.model.QueuedKey;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.builders.IReplicationStubBuilder;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;
import ish.oncourse.webservices.v4.stubs.replication.StubState;

import java.util.Map;

import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

public abstract class AbstractReplicationStubBuilder<T extends Queueable, V extends ReplicationStub> implements IReplicationStubBuilder {

	protected IReplicationStubBuilder next;
	protected Map<QueuedKey, QueuedRecord> queue;

	public AbstractReplicationStubBuilder(Map<QueuedKey, QueuedRecord> queue, IReplicationStubBuilder next) {
		this.queue = queue;
		this.next = next;
	}

	private T findMatchingEntity(QueuedRecord entity) {
		Class<T> entityClass = (Class<T>) entity.getObjectContext().getEntityResolver().getObjEntity(entity.getEntityIdentifier()).getClass();
		SelectQuery q = new SelectQuery(entityClass);
		q.andQualifier(ExpressionFactory.matchDbExp("id", entity.getEntityWillowId()));
		return DataObjectUtils.objectForPK(entity.getObjectContext(), entityClass, entity.getEntityWillowId());
	}

	public ReplicationStub convert(QueuedRecord record) {
		switch (record.getAction()) {
		case CREATE:
		case UPDATE:
			V stub = createEmptyStub();
			T entity = findMatchingEntity(record);
			fillStubValues(entity, stub);
			stub.setState(StubState.FULL);
			return stub;
		case DELETE:
			V deletedStub = createEmptyStub();
			deletedStub.setState(StubState.DELETED);
			return deletedStub;
		}

		return null;
	}

	public abstract V createEmptyStub();

	public abstract void fillStubValues(T entity, V stub);
}
