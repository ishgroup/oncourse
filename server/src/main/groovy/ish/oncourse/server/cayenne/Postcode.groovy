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

import ish.common.types.PostcodeType
import ish.oncourse.API
import ish.oncourse.server.cayenne.glue._Postcode

import javax.annotation.Nonnull
import javax.annotation.Nullable
import java.math.BigDecimal

/**
 * This table is read-only since it is updated through an automated process, pulling changes and new records
 * from an onCourse update service. That data is turn is fed from updates to Australia Post reference data.
 *
 */
class Postcode extends _Postcode {



    /**
	 *
	 * @return the latititude of the centre of the suburb
	 */
	@Nonnull
	@Override
	@API
    BigDecimal getLatitude() {
		return super.getLatitude()
    }

	/**
	 *
	 * @return the longitude of the centre of the suburb
	 */
	@Nonnull
	@Override
	@API
    BigDecimal getLongitude() {
		return super.getLongitude()
    }

	/**
	 *
	 * @return
	 */
	@Nonnull
	@Override
	@API
    PostcodeType getType() {
		return super.getType()
    }

	/**
	 *
	 * @return
	 */
	@Nullable
	@Override
	@API
    String getDescription() {
		return super.getDescription()
    }

	/**
	 *
	 * @return a four digit postcode, returned as a string
	 */
	@Nonnull
	@Override
	@API
    String getPostcode() {
		return super.getPostcode()
    }

	/**
	 *
	 * @return the 2-3 character string representation of the state
	 */
	@Nonnull
	@Override
	@API
    String getState() {
		return super.getState()
    }

	/**
	 * The suburb name may not be unique since a suburb may have several postcodes pointing to it
	 *
	 * @return the common name of the suburb
	 */
	@Nonnull
	@Override
	@API
    String getSuburb() {
		return super.getSuburb()
    }

}



