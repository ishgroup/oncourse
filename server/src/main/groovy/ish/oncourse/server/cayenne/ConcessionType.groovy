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
import ish.oncourse.cayenne.ConcessionTypeInterface
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._ConcessionType
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById

import javax.annotation.Nonnull
import java.util.Date
import java.util.List

/**
 * ConcessionTypes are a user definable set of concessions which can be linked to students. You might like to define
 * concessions like a Senior's Card or Centrelink card. Or you could create a concession for something less concrete
 * like 'staff member' or 'alumni'.
 *
 * The main purpose of these concessions is to control the application of discounts.
 */
@API
@QueueableEntity
class ConcessionType extends _ConcessionType implements ConcessionTypeInterface, Queueable {



	static ConcessionType concessionTypeForId(@Nonnull final ObjectContext context, @Nonnull Integer id) {
		return SelectById.query(ConcessionType.class, id).selectOne(context)
	}

	@Override
	void postAdd() {
		super.postAdd()
		if (getIsEnabled() == null) {
			setIsEnabled(true)
		}
		if (getHasExpiryDate() == null) {
			setHasExpiryDate(false)
		}
		if (getHasConcessionNumber() == null) {
			setHasConcessionNumber(false)
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
	 * @return true if entering of concession card number is required
	 */
	@Nonnull
	@API
	@Override
	Boolean getHasConcessionNumber() {
		return super.getHasConcessionNumber()
	}

	/**
	 * @return true if concession expiry date must be always specified
	 */
	@Nonnull
	@API
	@Override
	Boolean getHasExpiryDate() {
		return super.getHasExpiryDate()
	}


	/**
	 * @return true if this concession type is active
	 */
	@Nonnull
	@API
	@Override
	Boolean getIsEnabled() {
		return super.getIsEnabled()
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
	 * @return the name of this Concession
	 */
	@Nonnull
	@API
	@Override
	String getName() {
		return super.getName()
	}

	/**
	 * @return list of discounts linked to this concession type
	 */
	@Nonnull
	@API
	@Override
	List<DiscountConcessionType> getConcessionTypeDiscounts() {
		return super.getConcessionTypeDiscounts()
	}
}
