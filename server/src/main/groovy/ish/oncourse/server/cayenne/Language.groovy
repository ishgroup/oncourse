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
import ish.oncourse.server.cayenne.glue._Language
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import java.util.Date

/**
 * This table is read-only since it is updated through an automated process, pulling changes and new records
 * from an onCourse update service. That data is turn is fed from updates to the ABS (Australian Beureau of Statistics)
 * reference data.
 *
 * Note that the ABS will sometimes remove entries, however we will always preserve historic records.
 */
@API
class Language extends _Language {


	private static final Logger logger = LogManager.getLogger()

	/**
	 * @return The language code defined by the ABS
	 */
	@API
	@Override
	String getAbsCode() {
		return super.getAbsCode()
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
	 * When older languages are removed from the government standards list, they aren't deleted here but just disabled.
	 *
	 * @return whether this language is still available for use
	 */
	@Nonnull
	@API
	@Override
	Boolean getIsActive() {
		return super.getIsActive()
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
	 * @return the name of the language
	 */
	@Nonnull
	@API
	@Override
	String getName() {
		return super.getName()
	}
}
