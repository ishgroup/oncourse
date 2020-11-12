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
import ish.oncourse.server.cayenne.glue._DiscountMembership
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import java.util.Date
import java.util.List

/**
 * Object representing relation between discount and membership.
 */
@API
@QueueableEntity
class DiscountMembership extends _DiscountMembership implements Queueable {

	private static final Logger logger = LogManager.getLogger()

	/**
	 * @return true if discount can only be applied to the owner of the membership and can't be applied to their related contacts
	 */
	@Nonnull
	@API
	@Override
	Boolean getApplyToMemberOnly() {
		return super.getApplyToMemberOnly()
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
	 * @return the date and time this record was modified
	 */
	@API
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}



	/**
	 * @return linked discount
	 */
	@Nonnull
	@API
	@Override
	Discount getDiscount() {
		return super.getDiscount()
	}

	/**
	 * @return list of contact relation types which can use the discount along with the membership owner
	 */
	@Nonnull
	@API
	@Override
	List<DiscountMembershipRelationType> getDiscountMembershipRelationTypes() {
		return super.getDiscountMembershipRelationTypes()
	}

	/**
	 * @return linked membership product
	 */
	@Nonnull
	@API
	@Override
	MembershipProduct getMembershipProduct() {
		return super.getMembershipProduct()
	}
}
