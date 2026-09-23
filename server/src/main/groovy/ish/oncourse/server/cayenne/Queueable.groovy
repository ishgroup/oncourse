/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
package ish.oncourse.server.cayenne

import org.apache.cayenne.ObjectContext
import org.apache.cayenne.ObjectId

import javax.annotation.Nonnull
import javax.annotation.Nullable
/**
 * A persistent object which can be replicated to Willow
 */
trait Queueable {
    @Nonnull
	abstract Long getId();

	abstract void setWillowId(Long willowId);

	@Nullable
	abstract Long getWillowId();

	boolean isAsyncReplicationAllowed() {
		return true
	}

    /**
     * Performs custom validation logic that cannot be enforced using standard validation constraints,
     * such as checks involving relationships between this object and other persisted objects.
     *
     * This method is invoked during the replication process before the atomic group is committed.
     *
     * If this method throws a {@link ReplicationException}, the replication process for the entire atomic group
     * will be aborted.
     *
     * @param atomicContext the replication context providing access to related objects and transactional scope
     * @throws ReplicationException if the object fails validation and the replication must be aborted
     */
	void validateObject(ObjectContext atomicContext) throws ReplicationException {
	}

	// The following are from Persistent but still handy
	abstract ObjectId getObjectId()
	abstract ObjectContext getObjectContext();
	abstract void setObjectContext(ObjectContext objectContext);
}
