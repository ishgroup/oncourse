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
import ish.oncourse.server.cayenne.glue._TrainingPackage

import javax.annotation.Nonnull
import java.util.Date
import java.util.List

/**
 * The Training Package class contains records from training packages (national)
 * from the training.gov.au website. You cannot create or delete records in this table since they are
 * automatically kept in sync with data from the official website.
 *
 */
@API
class TrainingPackage extends _TrainingPackage {



	/**
	 * @return the date and time this record was created
	 */
	@API
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn() == null ? new Date() : super.getCreatedOn()
	}

	/**
	 * @return the date and time this record was modified
	 */
	@API
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn() == null ? new Date() : super.getModifiedOn()
	}

	/**
	 * @return
	 */
	@Override
	String getCopyrightCategory() {
		return super.getCopyrightCategory()
	}

	/**
	 * @return
	 */
	@Override
	String getCopyrightContact() {
		return super.getCopyrightContact()
	}

	/**
	 * @return
	 */
	@Override
	String getDeveloper() {
		return super.getDeveloper()
	}

	/**
	 * @return
	 */
	@Override
	Date getEndorsementFrom() {
		return super.getEndorsementFrom()
	}

	/**
	 * @return
	 */
	@Override
	Date getEndorsementTo() {
		return super.getEndorsementTo()
	}


	/**
	 * @return national ISC
	 */
	@API
	@Override
	String getNationalISC() {
		return super.getNationalISC()
	}

	/**
	 * @return
	 */
	@Override
	String getPurchaseFrom() {
		return super.getPurchaseFrom()
	}

	/**
	 * @return the title of the training package
	 */
	@API
	@Override
	String getTitle() {
		return super.getTitle()
	}

	/**
	 * @return
	 */
	@Override
	String getType() {
		return super.getType()
	}

	/**
	 * @return
	 */
	@Nonnull
	@Override
	List<Module> getModules() {
		return super.getModules()
	}

	/**
	 * @return
	 */
	@Nonnull
	@Override
	List<Qualification> getQualifications() {
		return super.getQualifications()
	}

	@Override
	String getSummaryDescription() {
		return getTitle()
	}
}
