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
import ish.oncourse.server.cayenne.glue._ContactRelationType
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import java.util.Date
import java.util.List

/**
 * Object representing type of relation between two contacts. For example parent-child, employer-employee, etc.
 */
@API
@QueueableEntity
class ContactRelationType extends _ContactRelationType implements Queueable {

	private static final Logger logger = LogManager.getLogger()

	@Override
	void postAdd() {
        super.postAdd()
		if (getDelegatedAccessToContact() == null) {
            setDelegatedAccessToContact(false)
		}
    }

	/**
	 * @return the date and time this record was created
	 */
	@API
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	/**
	 * @return true if contact on the left side of the relation can access portal account of the other related contact
	 */
	@Nonnull
	@API
	@Override
	Boolean getDelegatedAccessToContact() {
		return super.getDelegatedAccessToContact()
	}

	/**
	 * @return name of the left side of the relation, e.g. "parent", "employer", etc.
	 */
	@Nonnull
	@API
	@Override
	String getFromContactName() {
		return super.getFromContactName()
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
	 * @return name of the right side of the relation, e.g. "child", "employee", etc.
	 */
	@Nonnull
	@API
	@Override
	String getToContactName() {
		return super.getToContactName()
	}

	/**
	 * @return list of discount-membership relations linked to this contact relation type
	 */
	@Nonnull
	@API
	@Override
	List<DiscountMembershipRelationType> getMembeshipDiscountRelations() {
		return super.getMembeshipDiscountRelations()
	}
}
