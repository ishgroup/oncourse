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

import ish.oncourse.server.cayenne.glue._SavedFind

import javax.annotation.Nonnull
import java.util.Date

/**
 * Saved instance of advanced search.
 */
@Deprecated
class SavedFind extends _SavedFind {



	/**
	 * @return the date and time this record was created
	 */
	@Nonnull
	@Deprecated
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	/**
	 * @return string representation of Cayenne query expression
	 */
	@Nonnull
	@Deprecated
	@Override
	String getExpressionString() {
		return super.getExpressionString()
	}

	/**
	 * @return the date and time this record was modified
	 */
	@Nonnull
	@Deprecated
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	/**
	 * @return name of this saved find instance
	 */
	@Nonnull
	@Deprecated
	@Override
	String getName() {
		return super.getName()
	}

	/**
	 * @return name of entity this saved find searches for
	 */
	@Nonnull
	@Deprecated
	@Override
	String getTableName() {
		return super.getTableName()
	}

	/**
	 * @return user who created this saved find
	 */
	@Nonnull
	@Deprecated
	@Override
	SystemUser getSystemUser() {
		return super.getSystemUser()
	}
}
