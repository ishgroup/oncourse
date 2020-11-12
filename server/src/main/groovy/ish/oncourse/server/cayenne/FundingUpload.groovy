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

import ish.oncourse.types.FundingStatus
import ish.oncourse.API
import ish.oncourse.server.cayenne.glue._FundingUpload

import javax.annotation.Nonnull
import javax.annotation.Nullable

@API
class FundingUpload extends _FundingUpload {



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

	@API
	@Nullable
	@Override
	Long getOutcomeCount() {
		return super.getOutcomeCount()
	}

	@API
	@Nonnull
	@Override
	FundingStatus getStatus() {
		return super.getStatus()
	}

	@API
	@Nonnull
	@Override
	List<FundingUploadOutcome> getFundingUploadOutcomes() {
		return super.getFundingUploadOutcomes()
	}

	@API
	@Nonnull
	@Override
	SystemUser getSystemUser() {
		return super.getSystemUser()
	}
}



