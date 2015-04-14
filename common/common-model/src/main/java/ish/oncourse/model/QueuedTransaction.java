package ish.oncourse.model;

import ish.oncourse.model.auto._QueuedTransaction;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

import java.util.List;

public class QueuedTransaction extends _QueuedTransaction {
	
	private static final long serialVersionUID = -1318051832105725074L;
	
	/**
	 * Check if any record within transaction has reached max retry level.
	 * 
	 * @return true/false
	 */
	public boolean shouldSkipTransaction() {

		for (QueuedRecord r : getQueuedRecords()) {
			if (r.getNumberOfAttempts() >= QueuedRecord.MAX_NUMBER_OF_RETRY) {
				return true;
			}
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see ish.oncourse.model.auto._QueuedTransaction#getQueuedRecords()
	 */
	@Override
	public List<QueuedRecord> getQueuedRecords() {
		if (getObjectId().isTemporary()) {
			return super.getQueuedRecords();
		}
		else {
			SelectQuery q = new SelectQuery(QueuedRecord.class);
			q.andQualifier(ExpressionFactory.matchExp(QueuedRecord.QUEUED_TRANSACTION_PROPERTY, this));
			return getObjectContext().performQuery(q);
		}
	}
}
