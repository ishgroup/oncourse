
package ish.oncourse.model.access;

import org.apache.cayenne.DataChannel;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.access.ObjectStore;
import org.apache.log4j.Logger;


/**
 *
 * @author marek
 */
public class ISHDataContext extends DataContext {

	private boolean isRecordQueueingEnabled = true;

	private static final Logger logger = Logger.getLogger(ISHDataContext.class);


	/**
	 *
	 */
	public ISHDataContext() {
		super();
	}

	/**
	 * @param channel
	 * @param objectStore
	 */
	public ISHDataContext(DataChannel channel, ObjectStore objectStore) {
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
	 * @param isRecordQueueingEnbled set to true if record queueing is to be enabled (default)
	 */
	public void setRecordQueueingEnabled(boolean isRecordQueueingEnbled) {
		this.isRecordQueueingEnabled = isRecordQueueingEnbled;
	}

}
