package ish.oncourse.model;

import ish.oncourse.model.auto._QueuedRecord;

import java.util.Date;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.validation.ValidationResult;

public class QueuedRecord extends _QueuedRecord {

	private static final long serialVersionUID = 5511189858346011489L;
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

	/**
	 * Just in case check that college is the same for current record and its
	 * transaction.
	 */
	@Override
	protected void validateForSave(ValidationResult result) {
		super.validateForSave(result);
		QueuedTransaction transaction = getQueuedTransaction();
		if (!getCollege().getId().equals(transaction.getCollege().getId())) {
			result.addFailure(ValidationFailure.validationFailure(this, QueuedRecord.COLLEGE_PROPERTY, String.format(
					"QueuedRecord college:%s doesn't match QueuedTransaction college:%s.", getCollege().getId(), transaction.getCollege()
							.getId())));
		}
	}
}
