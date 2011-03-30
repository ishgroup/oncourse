package ish.oncourse.services.persistence;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.DataChannel;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.access.ObjectStore;

/**
 * 
 * @author marek
 */
public class ISHObjectContext extends DataContext {
	
	private ThreadLocal<String> transactionKeyStorage = new InheritableThreadLocal<String>();

	private boolean isRecordQueueingEnabled = true;
	
	public ISHObjectContext(DataChannel channel, ObjectStore objectStore) {
		super(channel, objectStore);
	}
	
	/**
	 * @return true if record queueing is enabled.
	 */
	public boolean getIsRecordQueueingEnabled() {
		return this.isRecordQueueingEnabled;
	}

	/**
	 * Determines whether to queue records for replication.
	 * 
	 * @param isRecordQueueingEnbled
	 *            set to true if record queueing is to be enabled (default)
	 */
	public void setRecordQueueingEnabled(boolean isRecordQueueingEnbled) {
		this.isRecordQueueingEnabled = isRecordQueueingEnbled;
	}

	/**
	 * Method, which assigns commit key to each commit attempt.
	 */
	@Override
	public void commitChanges() throws CayenneRuntimeException {
		String transactionKey = String.valueOf(this.hashCode()) + "_" + System.nanoTime();
		transactionKeyStorage.set(transactionKey);
		super.commitChanges();
	}

	/**
	 * Method, which returns the commit key of last commit on this context within current thread.
	 * @return
	 */
	public String getTransactionKey() {
		return transactionKeyStorage.get();
	}
}
