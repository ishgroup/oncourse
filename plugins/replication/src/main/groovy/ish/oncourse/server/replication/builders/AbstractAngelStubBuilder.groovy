/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.server.replication.builders

import ish.oncourse.server.cayenne.Queueable
import ish.oncourse.server.cayenne.QueuedRecord
import ish.oncourse.webservices.util.GenericReplicationStub
import ish.oncourse.webservices.v21.stubs.replication.DeletedStub
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 */
abstract class AbstractAngelStubBuilder<T extends Queueable, V extends GenericReplicationStub> implements IAngelStubBuilder {
	protected static final Logger logger = LogManager.getLogger()

	GenericReplicationStub convert(final QueuedRecord record) {
		GenericReplicationStub soapStub
		switch (record.getAction()) {
		case CREATE:
		case UPDATE:
			def entity = (T) record.getLinkedRecord()
			if (entity == null) {
				return null
			}
			soapStub = convert(entity)
			// this case can occur when we should commit thread inside replication context, but some objects should not be replicable
			if (soapStub == null) {
				return null
			}
			soapStub.setEntityIdentifier(record.getTableName())
			break
		case DELETE:
			soapStub = new DeletedStub()
			soapStub.setAngelId(record.getForeignRecordId())
			soapStub.setWillowId(record.getWillowId())
			soapStub.setEntityIdentifier(record.getTableName())
			break
		default:
			throw new IllegalArgumentException("QueuedRecord with null action is not allowed.")
		}
		return soapStub
	}

	/**
	 * @see IAngelStubBuilder#convert(Queueable)
	 */
	GenericReplicationStub convert(final Queueable entity) {
		final def soapStub = createFullStub((T) entity)
		soapStub.setAngelId(entity.getId())
		soapStub.setWillowId(entity.getWillowId())
		soapStub.setEntityIdentifier(entity.getObjectId().getEntityName())
		return soapStub
	}

	protected abstract V createFullStub(T entity)
}
