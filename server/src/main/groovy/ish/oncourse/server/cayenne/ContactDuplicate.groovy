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

import ish.common.types.ContactDuplicateStatus
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._ContactDuplicate

import java.util.Date

/**
 */
@QueueableEntity
class ContactDuplicate extends _ContactDuplicate implements Queueable {



	/**
	 * @return the date and time this record was created
	 */
	@API
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}


	/**
	 * @return the date and time this record was modified
	 */
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	/**
	 * @return
	 */
	@Override
	SystemUser getCreatedByUser() {
		return super.getCreatedByUser()
	}

	boolean logicAllowsReplication() {
		return ContactDuplicateStatus.IN_TRANSACTION == getStatus()
	}
}
