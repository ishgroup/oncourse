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
import ish.oncourse.server.cayenne.glue._DiscountMembershipRelationType
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import java.util.Date

/**
 * Object representing relation between DiscountMembership object and contact relation type.
 * Used to describe membership owner's contact relations which can use linked discount along with them.
 */
@API
@QueueableEntity
class DiscountMembershipRelationType extends _DiscountMembershipRelationType implements Queueable {

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
	 * @return linked contact relation type
	 */
	@Nonnull
	@API
	@Override
	ContactRelationType getContactRelationType() {
		return super.getContactRelationType()
	}

	/**
	 * @return linked DiscountMembership object
	 */
	@Nonnull
	@API
	@Override
	DiscountMembership getDiscountMembership() {
		return super.getDiscountMembership()
	}
}
