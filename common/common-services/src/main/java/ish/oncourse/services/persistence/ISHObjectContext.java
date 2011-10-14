package ish.oncourse.services.persistence;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.DataChannel;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.access.ObjectStore;
import org.apache.cayenne.graph.GraphDiff;

/**
 * 
 * @author marek
 */
public class ISHObjectContext extends DataContext {

	private static final String REPLICATING_PROP = "replicating";
	
	private static final String TRANSACTION_KEY_PROP = "transaction_key";

	public ISHObjectContext(DataChannel channel, ObjectStore objectStore) {
		super(channel, objectStore);
	}

	/**
	 * @return true if record queueing is enabled.
	 */
	public boolean getIsRecordQueueingEnabled() {
		return (Boolean) getUserProperty(REPLICATING_PROP);
	}

	/**
	 * Determines whether to queue records for replication.
	 * 
	 * @param isRecordQueueingEnbled
	 *            set to true if record queueing is to be enabled (default)
	 */
	public void setRecordQueueingEnabled(boolean isRecordQueueingEnbled) {
		setUserProperty(REPLICATING_PROP, isRecordQueueingEnbled);
	}

	/**
	 * Method, which returns the commit key of last commit on this context
	 * within current thread.
	 * 
	 * @return transaction key
	 */
	public String getTransactionKey() {
		return (String) getUserProperty(TRANSACTION_KEY_PROP);
	}

	/**
	 * Method, which assigns commit key to each commit attempt.
	 */
	@Override
	public GraphDiff onSync(ObjectContext originatingContext, GraphDiff changes, int syncType) {
		String transactionKey = String.valueOf(this.hashCode()) + System.nanoTime();
		setUserProperty(TRANSACTION_KEY_PROP, transactionKey);
		return super.onSync(originatingContext, changes, syncType); 
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.cayenne.access.DataContext#commitChanges()
	 */
	@Override
	public void commitChanges() throws CayenneRuntimeException {
		String transactionKey = String.valueOf(this.hashCode()) + System.nanoTime();
		setUserProperty(TRANSACTION_KEY_PROP, transactionKey);
		super.commitChanges();
	}
}
