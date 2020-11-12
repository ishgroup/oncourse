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

import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._ContactRelation
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import java.util.Date

/**
 * Object representing relation between two individual contacts. For example parent-child, employer-employee, etc.
 */
@API
@QueueableEntity
class ContactRelation extends _ContactRelation implements Queueable {

	private static final Logger logger = LogManager.getLogger()

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
	@API
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}



	/**
	 * @return contact on the left side of the relation
	 */
	@Nonnull
	@API
	@Override
	Contact getFromContact() {
		return super.getFromContact()
	}

	/**
	 * @return type of the contact relation
	 */
	@Nonnull
	@API
	@Override
	ContactRelationType getRelationType() {
		return super.getRelationType()
	}

	/**
	 * @return contact on the right side of the relation
	 */
	@Nonnull
	@API
	@Override
	Contact getToContact() {
		return super.getToContact()
	}
}
