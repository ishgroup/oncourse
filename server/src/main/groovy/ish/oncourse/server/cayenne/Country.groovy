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
import ish.oncourse.server.cayenne.glue._Country
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import java.util.Arrays
import java.util.Date
import java.util.List

/**
 * This table is read-only since it is updated through an automated process, pulling changes and new records
 * from an onCourse update service. That data is turn is fed from updates to the ABS (Australian Beureau of Statistics)
 * reference data.
 *
 * Note that the ABS will sometimes remove entries, however we will always preserve historic records.
 */
@API
class Country extends _Country {



	private static final Logger logger = LogManager.getLogger()


	/**
	 * List of SACC codes for all countries which are "Australia"
	 */
	@Nonnull
	private List<Integer> AUS_COUNTRY_SACC = Arrays.asList(0, 1100, 1101, 1102, 1603, 1199)

	static Country defaultCountry(@Nonnull final ObjectContext context) {

		final List<Country> results = ObjectSelect.query(Country.class).
				where(NAME.likeIgnoreCase("Australia")).
				select(context)

		if (results != null && results.size() > 0) {
			results.get(0)
		}

		return null
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
	 * @return the three character ISO code for the country
	 */
	@API
	@Override
	String getIsoCodeAlpha3() {
		return super.getIsoCodeAlpha3()
	}

	/**
	 * @return the ISO numeric code for the country
	 */
	@API
	@Override
	Integer getIsoCodeNumeric() {
		return super.getIsoCodeNumeric()
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
	 * @return the human readable name for the country
	 */
	@Nonnull
	@API
	@Override
	String getName() {
		return super.getName()
	}

	/**
	 * @return the numerical statistical code for the country. This one is used for AVETMISS reporting.
	 */
	@API
	@Override
	Integer getSaccCode() {
		return super.getSaccCode()
	}


	/**
	 * The countries in this table are not unique. For example, there are at least five records pointing to Australia.
	 *
	 * @return true if this country is one of the "Australia" countries
	 */
	@API
	boolean isAustralia() {
		return AUS_COUNTRY_SACC.contains(getSaccCode())

	}
}
