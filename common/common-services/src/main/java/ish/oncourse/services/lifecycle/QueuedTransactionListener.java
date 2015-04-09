package ish.oncourse.services.lifecycle;

import ish.oncourse.model.QueuedRecord;
import ish.oncourse.model.QueuedTransaction;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.annotation.PostUpdate;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

import java.util.List;

/**
 * Checks if current transaction is empty and removes if needed.
 * 
 * @author anton
 */
public class QueuedTransactionListener {

	/**
	 * Cayenne service.
	 */
	private ICayenneService cayenneService;

	public QueuedTransactionListener(ICayenneService cayenneService) {
		super();
		this.cayenneService = cayenneService;
	}

	@PostUpdate(value = QueuedTransaction.class)
	public void postUpdate(QueuedTransaction t) {
		
		SelectQuery q = new SelectQuery(QueuedRecord.class);
		q.andQualifier(ExpressionFactory.matchExp(QueuedRecord.QUEUED_TRANSACTION_PROPERTY, t));
		@SuppressWarnings("unchecked")
		List<QueuedRecord> list = t.getObjectContext().performQuery(q);

		if (list.isEmpty()) {
			ObjectContext objectContext = cayenneService.newNonReplicatingContext();
			objectContext.deleteObject(objectContext.localObject(t));
			objectContext.commitChanges();
		}
	}
}
