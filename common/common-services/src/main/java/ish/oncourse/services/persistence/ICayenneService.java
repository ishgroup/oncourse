package ish.oncourse.services.persistence;

import org.apache.cayenne.DataChannel;
import org.apache.cayenne.ObjectContext;

/**
 * A service wrapper around Cayenne stack returning appropriately scoped
 * contexts.
 */
public interface ICayenneService {

	/**
	 * Creates and returns a new read-write context on every invocation.
	 */
	ObjectContext newContext();

	/**
	 * A read-only shared context reused between requests and threads.
	 */
	ObjectContext sharedContext();
	
	/**
	 * A context in which updates do not trigger new QueuedRecords.
	 * @return
	 */
	ObjectContext newNonReplicatingContext();


	ObjectContext newContext(DataChannel parentChannel);
}
