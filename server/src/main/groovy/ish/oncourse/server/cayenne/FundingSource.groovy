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
import ish.oncourse.common.ExportJurisdiction
import ish.oncourse.server.cayenne.glue._FundingSource

import javax.annotation.Nonnull
import javax.annotation.Nullable
import java.util.Date
import java.util.List

@API
class FundingSource extends _FundingSource {



    /**
	 *
	 * @return true if active
	 */
	@API
	@Nonnull
	@Override
    Boolean getActive() {
		return super.getActive()
    }

	/**
	 *
	 * @return flavour of this funding source
	 */
	@API
	@Nonnull
	@Override
    ExportJurisdiction getFlavour() {
		return super.getFlavour()
    }

	/**
	 *
	 * @return name of this funding source
	 */
	@API
	@Nonnull
	@Override
    String getName() {
		return super.getName()
    }

	/**
	 *
	 * @return enrolments created using this funding source
	 */
	@API
	@Nonnull
	@Override
    List<Enrolment> getEnrolments() {
		return super.getEnrolments()
    }

	/**
	 * @return the date and time this record was created
	 */
	@API
	@Nullable
	@Override
    Date getCreatedOn() {
		return super.getCreatedOn()
    }

	/**
	 * @return the date and time this record was modified
	 */
	@API
	@Nullable
	@Override
    Date getModifiedOn() {
		return super.getModifiedOn()
    }
}



