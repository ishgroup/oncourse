package ish.oncourse.webservices.builders.replication;

import ish.oncourse.model.Queueable;
import ish.oncourse.model.QueuedKey;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.builders.IReplicationStubBuilder;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;

import java.util.Map;

import org.apache.cayenne.DataObjectUtils;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

public abstract class AbstractReplicationStubBuilder implements IReplicationStubBuilder {

	protected IReplicationStubBuilder next;
	protected Map<QueuedKey, QueuedRecord> queue;

	public AbstractReplicationStubBuilder(Map<QueuedKey, QueuedRecord> queue, IReplicationStubBuilder next) {
		this.queue = queue;
		this.next = next;
	}

	protected Queueable findMatchingEntity(QueuedRecord entity) {

		Class<?> entityClass = entity.getObjectContext().getEntityResolver().getObjEntity(entity.getEntityIdentifier()).getClass();
		SelectQuery q = new SelectQuery(entityClass);
		q.andQualifier(ExpressionFactory.matchDbExp("id", entity.getEntityWillowId()));

		return (Queueable) DataObjectUtils.objectForPK(entity.getObjectContext(), entityClass, entity.getEntityWillowId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ish.oncourse.webservices.builders.IReplicationStubBuilder#convert(ish
	 * .oncourse.model.QueuedRecord)
	 */
	@Override
	public ReplicationStub convert(QueuedRecord entity) {
		switch (entity.getAction()) {
		case CREATE:
		case UPDATE:
			return createFullStub(entity);
		case DELETE:
			return createDeletedStub(entity);
		}

		return null;
	}

	public abstract ReplicationStub createFullStub(QueuedRecord entity);

	public abstract ReplicationStub createDeletedStub(QueuedRecord entity);
}
