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

import ish.common.types.AccountType
import ish.oncourse.API
import ish.oncourse.cayenne.AccountInterface
import ish.oncourse.server.cayenne.glue._Account
import org.apache.cayenne.PersistenceState
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import javax.annotation.Nullable
/**
 * An Account is a representation of a General Ledger transactional account for the finance backend of onCourse.
 *
 */
@API
class Account extends _Account implements AccountInterface {



	private static Logger logger = LogManager.getLogger()

	/**
	 *
	 * @return  true if the account is an asset type
	 */
	@API
	boolean isAsset() {
		return AccountType.ASSET == getType()
	}

	/**
	 *
	 * @return  true if the account is an income type
	 */
	@API
	boolean isIncome() {
		return AccountType.INCOME == getType()
	}

	/**
	 *
	 * @return  true if the account is an expense type
	 */
	@API
	boolean isExpense() {
		return AccountType.EXPENSE == getType()
	}

	/**
	 *
	 * @return  true if the account is a liability type
	 */
	@API
	boolean isLiability() {
		return AccountType.LIABILITY == getType()
	}

	/**
	 *
	 * @return  true if the account is a cost of sale type (this is a particular type of expense)
	 */
	@API
	boolean isCOS() {
		return AccountType.COS == getType()
	}

	/**
	 * Equity account types are rarely used in onCourse.
	 *
	 * @return  true if the account is an equity type
	 */
	@API
	boolean isEquity() {
		return AccountType.EQUITY == getType()
	}

	/**
	 * Is this account one of the debit types.
	 * * ASSET
	 * * COS
	 * * EQUITY
	 * * EXPENSE
	 *
	 * @return  true if the account is an equity type
	 */
	@API
	boolean isDebit() {
		return AccountType.DEBIT_TYPES.contains(getType())
	}

	/**
	 * Is this account one of the credit types.
	 * * LIABILITY
	 * * INCOME
	 *
	 * @return  true if the account is an equity type
	 */
	@API
	boolean isCredit() {
		return AccountType.CREDIT_TYPES.contains(getType())
	}

	@Override
	void setPersistenceState(final int persistenceState) {
		logger.debug("changing persistence state from: {} to: {}",
				PersistenceState.persistenceStateName(getPersistenceState()),
				PersistenceState.persistenceStateName(persistenceState))
		super.setPersistenceState(persistenceState)
	}

	boolean isExportableReferencable() {
		return true
	}

	/**
	 * @return an account code as used in an external accounting system or general ledger
	 */
	@Nonnull
	@API
	@Override
	String getAccountCode() {
		return super.getAccountCode()
	}

	/**
	 * @return the date and time this record was created
	 */
	@Nullable
	@API
	@Override
	Date getCreatedOn() {
		return super.getCreatedOn()
	}

	/**
	 * @return a description which will appear in the user interface alongside the account code
	 */
	@Nonnull
	@API
	@Override
	String getDescription() {
		return super.getDescription()
	}

	/**
	 * Accounts cannot be deleted once used, so you can only disable them in order to prevent them from being used in the future.
	 * Note that disabling an account doesn't "turn it off", it only hides that account from being selected in the user
	 * interface. Existing records already linked to that account will continue to be linked and use that account.
	 *
	 * @return whether the account is enabled
	 */
	@Nonnull
	@API
	@Override
	Boolean getIsEnabled() {
		return super.getIsEnabled()
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
	 * @return the type of account (asset, liability, etc)
	 */
	@Nonnull
	@API
	@Override
	AccountType getType() {
		return super.getType()
	}

	@Override
	String getSummaryDescription() {
		return getAccountCode()
	}

	List<AbstractInvoiceLine> getAbstractInvoiceLines() {
		return new ArrayList<AbstractInvoiceLine>(){{
			addAll(invoiceLines)
			addAll(quoteLines)
		}}
	}
}
