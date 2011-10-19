package ish.oncourse.model;

import java.util.Date;

import ish.oncourse.model.auto._QueuedRecord;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

public class QueuedRecord extends _QueuedRecord {
	
	/**
	 * Maximum retry number.
	 */
	public static final Integer MAX_NUMBER_OF_RETRY = 5;

	public QueuedRecord() {
		super();
	}

	public QueuedRecord(QueuedRecordAction action, String entityIdentifier, Long entityWillowId) {
		setAction(action);
		setEntityIdentifier(entityIdentifier);
		setEntityWillowId(entityWillowId);
	}

	public Queueable getLinkedRecord() {
		@SuppressWarnings("unchecked")
		Class<? extends Queueable> entityClass = (Class<? extends Queueable>) getObjectContext().getEntityResolver()
				.getObjEntity(getEntityIdentifier()).getJavaClass();

		SelectQuery q = new SelectQuery(entityClass);
		q.andQualifier(ExpressionFactory.matchDbExp("id", getEntityWillowId()));
		q.andQualifier(ExpressionFactory.matchExp("college", getCollege()));

		return (Queueable) Cayenne.objectForQuery(getObjectContext(), q);
	}

	@Override
	protected void onPreRemove() {
		getQueuedTransaction().setModified(new Date());
	}
}
