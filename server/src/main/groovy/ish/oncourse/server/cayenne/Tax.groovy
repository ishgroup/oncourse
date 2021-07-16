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

import ish.common.payable.TaxInterface
import ish.oncourse.API
import ish.oncourse.cayenne.QueueableEntity
import ish.oncourse.server.cayenne.glue._Tax
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.ObjectSelect
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
/**
 */
@API
@QueueableEntity
class Tax extends _Tax implements TaxInterface, Queueable {

	private static final String DESCRIPTION_KEY = "description";
	private static final Logger logger = LogManager.getLogger()

	@Override
	void onEntityCreation() {
		super.onEntityCreation()
		// those fields are set to default on client to pass validaiton
		if (getIsGSTTaxType() == null) {
			setIsGSTTaxType(Boolean.FALSE)
		}
	}

	/**
	 * @return
	 */
	static Tax getTaxWithCode(final String taxCode, @Nonnull ObjectContext context) throws Exception {
		List<Tax> taxes = ObjectSelect.query(Tax.class).
				where(TAX_CODE.eq(taxCode))
				.select(context)

		if (taxes.size() != 1) {
			throw new Exception("Found " + taxes.size() + " taxes with code " + taxCode + ".")
		}
		return taxes.get(0)
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
	 * @return
	 */
	@API
	@Override
	String getDescription() {
		return super.getDescription()
	}


	/**
	 * @return
	 */
	@Nonnull
	@API
	@Override
	Boolean getIsGSTTaxType() {
		return super.getIsGSTTaxType()
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
	 * @return the rate of this tax expressed as a decimal (so a 10% tax would return 0.1)
	 */
	@API
	@Override
	BigDecimal getRate() {
		return super.getRate()
	}

	/**
	 * @return
	 */
	@Nonnull
	@API
	@Override
	String getTaxCode() {
		return super.getTaxCode()
	}

	/**
	 * @return
	 */
	@Nonnull
	@API
	@Override
	Account getPayableToAccount() {
		return super.getPayableToAccount()
	}

	/**
	 * @return
	 */
	@Nonnull
	@API
	@Override
	Account getReceivableFromAccount() {
		return super.getReceivableFromAccount()
	}
}
