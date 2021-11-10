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
import ish.oncourse.server.cayenne.glue._DefinedTutorRole
import org.apache.cayenne.query.ObjectSelect

import javax.annotation.Nonnull
import javax.annotation.Nullable

/**
 * Tutor roles represent the type of engagement a tutor has in a class. They might be a 'trainer', 'assistant' or
 * 'assessor' or any number of other roles. The DefinedTutorRole objects are set up in preferences and typically not changed
 * often.
 *
 * Each role can have a set of pay rates, although the specific pay rate can also be overridden per tutor per class.
 */
@API
class DefinedTutorRole extends _DefinedTutorRole {

	public static final String CURRENT_PAYRATE = "currentPayrate"

	@API
	PayRate getCurrentPayrate() {
		return getPayRateForDate(new Date())
	}

	@Override
	void onEntityCreation() {
		if (getActive() == null) {
			setActive(true)
		}
		super.onEntityCreation()
	}

	/**
	 * For the start date of a particular class, get the pay rate for this role. If the class has no schedule,
	 * and therefore no start date, use today's date.
	 *
	 * @param courseclass calculate the payrate for a particular class
	 * @return the applicable pay rate
	 */
	@Nullable
	@API
	PayRate getPayRateForCourseClass(@Nullable CourseClass courseclass) {
		if (courseclass == null) {
			return null
		} else if (courseclass.getStartDateTime() == null) {
			return getPayRateForDate(new Date())
		} else {
			return getPayRateForDate(courseclass.getStartDateTime())
		}
	}

	/**
	 * For a particular session, get the pay rate. If you pass a session without a defined start date, then use
	 * today's date.
	 *
	 * @param session calculate the payrate for a particular session
	 * @return the applicable pay rate
	 */
	@Nullable
	@API
	PayRate getPayRateForSession(@Nullable Session session) {
		if (session == null) {
			return null
		} else if (session.getStartDatetime() == null) {
			return getPayRateForDate(new Date())
		} else {
			return getPayRateForDate(session.getStartDatetime())
		}
	}

	/**
	 * For a particular date, get the rate which is valid. If nothing is applicable, return null
	 *
	 * @param date date to get payrate for
	 * @return the applicable pay rate
	 */
	@Nullable
	@API
	PayRate getPayRateForDate(Date date) {
		List<PayRate> rates = (ObjectSelect.query(PayRate.class).
				where(PayRate.VALID_FROM.lte(date)) & PayRate.DEFINED_TUTOR_ROLE.eq(this)).
				orderBy(PayRate.VALID_FROM.desc()).
				select(getContext())

		if (rates.size() == 0) {
			return null
		}
		return rates.get(0)
	}

	/**
	 * @return the description for this tutor role
	 */
	@API
	@Override
	String getDescription() {
		return super.getDescription()
	}


	/**
	 * @return the name of this role
	 */
	@Nonnull
	@API
	@Override
	String getName() {
		return super.getName()
	}

	/**
	 * Pay rates can be given a starting date in which case the new rate takes over from the previous one on that date.
	 * In this way forward planning for pay rises can be built in.
	 *
	 * @return a list of applicable pay rates for this role
	 */
	@Nonnull
	@API
	@Override
	List<PayRate> getPayRates() {
		return super.getPayRates()
	}

	/**
	 * Tutor roles need the capacity to be retired.
	 * So if active is false they no longer appear in the drop down list inside the class add tutor process
	 *
	 * @return true if TutorRole is active and can be applied for class
	 */
	@Nonnull
	@API
	@Override
	Boolean getActive() {
		return super.getActive()
	}
}
