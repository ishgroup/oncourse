package ish.oncourse.services.persistence;

import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.DataChannel;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.access.ObjectStore;
import org.apache.cayenne.graph.GraphDiff;
import org.apache.commons.codec.digest.DigestUtils;

import java.security.SecureRandom;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 
 * @author marek
 */
public class ISHObjectContext extends DataContext {
	private static final long serialVersionUID = -4572980938171252406L;

	/**
	 * Property which shows whether replication enabled on the object context.
	 */
	private static final String REPLICATING_PROP = "replicating";
	
	public static final String DEFAULT_CACHE_GROUP = "defaultGroup";
		
	/**
	 * We use put the copy of generated transaction key into stack, because there might be nested calls of commitChanges() from callbacks on the same object
	 * context.
	 */
	private Deque<String> transactionKeyStack = new ArrayDeque<>();

	private ISHObjectContext parentContext;

	public ISHObjectContext(DataChannel channel, ObjectStore objectStore) {
		super(channel, objectStore);
	}
	
	/**
	 * Generates new transaction key.
	 * 
	 * @return transaction key
	 */
	private String generateTransactionKey() {
		SecureRandom random = new SecureRandom(String.valueOf(this.hashCode()).getBytes());
		byte bytes[] = new byte[20];
		random.nextBytes(bytes);
		return DigestUtils.md5Hex(bytes);
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
		return transactionKeyStack.peek();
	}

	/**
	 * Method, which assigns commit key to each commit attempt.
	 */
	@Override
	public GraphDiff onSync(ObjectContext originatingContext, GraphDiff changes, int syncType) {
		String transactionKey = transactionKeyStack.isEmpty() ? generateTransactionKey() : transactionKeyStack.peek();
		transactionKeyStack.push(transactionKey);
		GraphDiff diff = super.onSync(originatingContext, changes, syncType); 
		transactionKeyStack.pop();
		return diff;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.cayenne.access.DataContext#commitChanges()
	 */
	@Override
	public void commitChanges() throws CayenneRuntimeException {
		String transactionKey = transactionKeyStack.isEmpty() ? generateTransactionKey() : transactionKeyStack.peek();
		transactionKeyStack.push(transactionKey);
		super.commitChanges();
		transactionKeyStack.pop();
	}
}
