package ish.oncourse.model;

import ish.oncourse.model.auto._QueuedRecord;
import ish.oncourse.utils.QueueableObjectUtils;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.validation.ValidationResult;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

public class QueuedRecord extends _QueuedRecord {

	/**
	 * Logger
	 */
	private static final Logger logger = LogManager.getLogger();

	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 5511189858346011489L;

	/**
	 * Maximum retry number.
	 */
	public static final Integer MAX_NUMBER_OF_RETRY = 3;

	public QueuedRecord() {
		super();
	}

	public QueuedRecord(QueuedRecordAction action, String entityIdentifier, Long entityWillowId) {
		setAction(action);
		setEntityIdentifier(entityIdentifier);
		setEntityWillowId(entityWillowId);
	}

	public Long getId() {
		return QueueableObjectUtils.getId(this);
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

	@Override
	public void setNumberOfAttempts(Integer numberOfAttempts) {
		super.setNumberOfAttempts(numberOfAttempts);
		setLastAttemptTimestamp(new Date());
	}

	/*
	 * @see
	 * ish.oncourse.model.auto._QueuedRecord#setErrorMessage(java.lang.String)
	 */
	@Override
	public void setErrorMessage(String errorMessage) {
		super.setErrorMessage(StringUtils.abbreviate(errorMessage, 1024));
		if (QueuedRecord.MAX_NUMBER_OF_RETRY.equals(getNumberOfAttempts())) {
			logger.error("Max number of retries has been reached for QueuedRecord entityIdentifier: {} and willowId: {} in transaction: {} with errorMessage: {}",
					getEntityIdentifier(), getEntityWillowId(), getQueuedTransaction().getObjectId(), errorMessage);
		}
	}
}
