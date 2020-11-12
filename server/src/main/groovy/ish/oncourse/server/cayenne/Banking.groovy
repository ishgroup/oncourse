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
import ish.oncourse.cayenne.BankingInterface
import ish.oncourse.common.BankingType
import ish.oncourse.server.cayenne.glue._Banking
import ish.util.LocalDateUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import javax.annotation.Nullable
import java.time.LocalDate
import java.util.Date
import java.util.List

@API
class Banking extends _Banking implements BankingInterface {
	private static final Logger logger = LogManager.getLogger()


	/**
	 * @return the date and time this record was created
	 */
	@Nullable
	@Override
	@API
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	/**
	 * @return the date and time this record was modified
	 */
	@Nullable
	@Override
	@API
	Date getModifiedOn() {
		return super.getModifiedOn()
	}

	/**
	 * The settlement date represents the date on which money should be available to a bank account.
	 *
	 * @return which money should be available
	 */
	@Nonnull
	@Override
	@API
	LocalDate getSettlementDate() {
		return super.getSettlementDate()
	}

	/**
	 * If the banking was done manually then this is the location (site) at which the banking
	 * was done. This can be useful to group cash and cheque banking performed at a single location.
	 *
	 * @return admin site
	 */
	@Nullable
	@Override
	@API
	Site getAdminSite() {
		return super.getAdminSite()
	}

	/**
	 * The user who created the banking. Null if it was generated automatically.
	 *
	 * @return user
	 */
	@Nullable
	@Override
	@API
	SystemUser getCreatedBy() {
		return super.getCreatedBy()
	}

	/**
	 * All the payments-in banked in this batch
	 *
	 * @return list of payments-in
	 */
	@Nonnull
	@Override
	@API
	List<PaymentIn> getPaymentsIn() {
		return super.getPaymentsIn()
	}

	/**
	 * All the payments-out banked in this batch
	 *
	 * @return list of payments-out
	 */
	@Nonnull
	@Override
	@API
	List<PaymentOut> getPaymentsOut() {
		return super.getPaymentsOut()
	}

	/**
	 * The type of banking event this record represents. Typically this is separated into
	 * automatic gateway banking which is created once a day, and
	 *
	 * @return banking type
	 */
	@Nonnull
	@Override
	@API
	BankingType getType() {
		return super.getType()
	}

	@Override
	String getSummaryDescription() {
		return getSettlementDate() == null
				? super.getSummaryDescription()
				: LocalDateUtils.valueToString(getSettlementDate())
	}
}



