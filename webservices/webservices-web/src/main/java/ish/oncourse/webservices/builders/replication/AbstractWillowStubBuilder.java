package ish.oncourse.webservices.builders.replication;

import ish.oncourse.model.Queueable;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.v4.stubs.replication.DeletedStub;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

public abstract class AbstractWillowStubBuilder<T extends Queueable, V extends ReplicationStub> implements IWillowStubBuilder {

	private Queueable findEntityByWillowId(QueuedRecord record) {
		
		@SuppressWarnings("unchecked")
		Class<? extends Queueable> entityClass = (Class<? extends Queueable>) record.getObjectContext().getEntityResolver()
				.getObjEntity(record.getEntityIdentifier()).getJavaClass();

		SelectQuery q = new SelectQuery(entityClass);
		q.andQualifier(ExpressionFactory.matchDbExp("id", record.getEntityWillowId()));
		q.andQualifier(ExpressionFactory.matchExp("college", record.getCollege()));
		
		return (Queueable) Cayenne.objectForQuery(record.getObjectContext(), q);
	}

	public ReplicationStub convert(QueuedRecord record) {
		
		@SuppressWarnings("unchecked")
		T entity = (T) findEntityByWillowId(record);
		
		switch (record.getAction()) {
		case CREATE:
		case UPDATE:
			ReplicationStub fullStub = createFullStub(entity);
			fullStub.setEntityIdentifier(record.getEntityIdentifier());
			fullStub.setWillowId(record.getEntityWillowId());
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
