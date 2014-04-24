package ish.oncourse.webservices.replication.v4.builders;

import ish.oncourse.model.Queueable;
import ish.oncourse.model.QueuedRecord;
import ish.oncourse.webservices.util.SupportedVersions;
import ish.oncourse.webservices.util.GenericReplicationStub;

public interface IWillowStubBuilder {
	/**
	 * Converts record from the replication queue to correspondent soap stub.
	 * 
	 * @param queuedRecord record from the queue.
	 * @return soap stub
	 */
	GenericReplicationStub convert(final QueuedRecord entity, final SupportedVersions version);
	
	/**
	 * Converts queueable entity to correspondent soap stub.
	 * 
	 * @param entity queueable entity
	 * @return soap stub.
	 */
	GenericReplicationStub convert(final Queueable entity, final SupportedVersions version);
}
