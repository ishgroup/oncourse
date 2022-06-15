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
	private boolean replicationAllowed = true
    @Nonnull
	abstract Long getId();

	abstract void setWillowId(Long willowId);

	@Nullable
	abstract Long getWillowId();

	boolean isAsyncReplicationAllowed(){
		return replicationAllowed && logicAllowsReplication()
	}

	void setAsyncReplicationAllowed(boolean value){
		this.replicationAllowed = value
	}

	boolean logicAllowsReplication() {
		return replicationAllowed
	}
	
	// The following are from Persistent but still handy
	abstract ObjectId getObjectId()
	abstract ObjectContext getObjectContext();
	abstract void setObjectContext(ObjectContext objectContext);
}
