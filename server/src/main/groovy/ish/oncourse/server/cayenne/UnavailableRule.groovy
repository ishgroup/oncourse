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

import ish.common.types.SessionRepetitionType
import ish.oncourse.API
import ish.oncourse.server.cayenne.glue._UnavailableRule

import javax.annotation.Nonnull
import java.util.Date

/**
 * Rule determining period of time when room or tutor is unavailable.
 */
@API
class UnavailableRule extends _UnavailableRule implements UnavailableRuleTrait {



	@Override
	void postAdd() {
		super.postAdd()

		if (super.getAllDay() == null) {
			setAllDay(false)
		}

		if (getRecurrenceFrequency() == null) {
			setRecurrenceFrequency(SessionRepetitionType.NONE_CHOICE)
		}
	}

	/**
	 * @return if unavailability spans for the whole day
	 */
	@Nonnull
	@API
	@Override
	Boolean getAllDay() {
		return super.getAllDay()
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
	 * @return end date and time of unavailability
	 */
	@Nonnull
	@API
	@Override
	Date getEndDateTime() {
		return super.getEndDateTime()
	}

	/**
	 * @return explanation fot the unavailability
	 */
	@API
	@Override
	String getExplanation() {
		return super.getExplanation()
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
	 * @return unavailability repeat type
	 */
	@Override
	SessionRepetitionType getRecurrenceFrequency() {
		return super.getRecurrenceFrequency()
	}

	/**
	 * @return unavailability repeat interval if custom repeat type is chosen
	 */
	@API
	@Override
	Integer getRecurrenceInterval() {
		return super.getRecurrenceInterval()
	}

	/**
	 * @return number of times this rule will repeat
	 */
	@API
	@Override
	Integer getRepetitionCount() {
		return super.getRepetitionCount()
	}

	/**
	 * @return date and time when unavailability starts
	 */
	@Nonnull
	@API
	@Override
	Date getStartDateTime() {
		return super.getStartDateTime()
	}

	/**
	 * @return date and time until unavailability is will be repeated
	 */
	@API
	@Override
	Date getUntilDateTime() {
		return super.getUntilDateTime()
	}
}
