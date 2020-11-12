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
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._CorporatePassProduct

import javax.annotation.Nonnull
import java.util.Date

/**
 * Object representing relation between CorporatePass and product.
 * CorporatePass can only be used for purchasing products which are linked to it.
 */
@API
@QueueableEntity
class CorporatePassProduct extends _CorporatePassProduct implements Queueable{



	/**
	 * @return the date and time this record was created
	 */
	@API
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
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
	 * @return CorporatePass record
	 */
	@Nonnull
	@API
	@Override
	CorporatePass getCorporatePass() {
		return super.getCorporatePass()
	}

	/**
	 * @return product record
	 */
	@Nonnull
	@API
	@Override
	Product getProduct() {
		return super.getProduct()
	}
}
