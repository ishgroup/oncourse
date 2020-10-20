/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.commercial.replication.builders

import ish.oncourse.server.cayenne.Queueable
import ish.oncourse.commercial.cayenne.QueuedRecord
import ish.oncourse.webservices.util.GenericReplicationStub

/**
 */
interface IAngelStubBuilder {
	/**
	 * Converts record from the replication queue to correspondent soap stub.
	 *
	 * @param queuedRecord record from the queue.
	 * @return soap stub
	 */
	GenericReplicationStub convert(QueuedRecord queuedRecord)

	/**
	 * Converts queueable entity to correspondent soap stub.
	 *
	 * @param entity queueable entity
	 * @return soap stub.
	 */
	GenericReplicationStub convert(Queueable entity)
}
