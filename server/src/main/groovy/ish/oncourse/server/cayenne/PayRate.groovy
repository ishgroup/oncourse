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

import ish.common.types.ClassCostRepetitionType
import ish.math.Money
import ish.oncourse.API
import ish.oncourse.server.cayenne.glue._PayRate

import javax.annotation.Nonnull

/**
 * A persistent class mapped as "PayRate" Cayenne entity.
 */
@API
class PayRate extends _PayRate {



	/**
	 * @return the date and time this record was created
	 */
	@Nonnull
	@API
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	/**
	 * @return the description of this payrate
	 */
	@API
	@Override
	String getDescription() {
		return super.getDescription()
	}

	/**
	 * @return the date and time this record was modified
	 */
	@Nonnull
	@API
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	//TODO docs
	/**
	 * @return
	 */
	@Nonnull
	@API
	@Override
	BigDecimal getOncostRate() {
		return super.getOncostRate()
	}

	/**
	 * @return the rate of pay
	 */
	@Nonnull
	@API
	@Override
	Money getRate() {
		return super.getRate()
	}

	/**
	 * @return repition type of this payrate
	 */
	@Nonnull
	@API
	@Override
	ClassCostRepetitionType getType() {
		return super.getType()
	}

	/**
	 * @return the date from when this payrate is valid
	 */
	@Nonnull
	@API
	@Override
	Date getValidFrom() {
		return super.getValidFrom()
	}

	/**
	 * @return the DefinedTutorRole this payrate is applied to
	 */
	@Nonnull
	@API
	@Override
	DefinedTutorRole getDefinedTutorRole() {
		return super.getDefinedTutorRole()
	}

	/**
	 * @return the last user who has edited this record
	 */
	@Nonnull
	@API
	@Override
	SystemUser getEditedByUser() {
		return super.getEditedByUser()
	}

	@Override
	String getSummaryDescription() {
		return getDescription()
	}
}
