package ish.oncourse.services.lifecycle;

import ish.oncourse.model.QueuedRecord;
import ish.oncourse.model.QueuedTransaction;
import ish.oncourse.services.persistence.ICayenneService;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.annotation.PostUpdate;
import org.apache.cayenne.query.ObjectSelect;

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
		this.cayenneService = cayenneService;
	}

	@PostUpdate(value = QueuedTransaction.class)
	public void postUpdate(QueuedTransaction t) {

		List<QueuedRecord> list = ObjectSelect.query(QueuedRecord.class, QueuedRecord.QUEUED_TRANSACTION.eq(t))
				.select(t.getObjectContext());

		if (list.isEmpty()) {
			ObjectContext objectContext = cayenneService.newNonReplicatingContext();
			objectContext.deleteObject(objectContext.localObject(t));
			objectContext.commitChanges();
		}
	}
}
