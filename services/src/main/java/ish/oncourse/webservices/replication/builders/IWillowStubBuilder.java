package ish.oncourse.webservices.replication.builders;

import ish.oncourse.model.Queueable;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.v4.stubs.replication.ReplicationStub;

public interface IWillowStubBuilder {
	/**
	 * Converts record from the replication queue to correspondent soap stub.
	 * 
	 * @param queuedRecord record from the queue.
	 * @return soap stub
	 */
	ReplicationStub convert(QueuedRecord entity);
	
	/**
	 * Converts queueable entity to correspondent soap stub.
	 * 
	 * @param entity queueable entity
	 * @return soap stub.
	 */
	ReplicationStub convert(Queueable entity);
}
