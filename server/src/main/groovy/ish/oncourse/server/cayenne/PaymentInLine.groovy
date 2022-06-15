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


import ish.math.Money
import ish.oncourse.API
import ish.oncourse.cayenne.*
import ish.oncourse.server.cayenne.glue._PaymentInLine
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import javax.annotation.Nullable

/**
 * Payment line links payment with one or multiple invoices which are getting paid for.
 */
@API
@QueueableEntity
class PaymentInLine extends _PaymentInLine implements PaymentInLineInterface, Queueable {

	private static final Logger logger = LogManager.getLogger()
	/**
	 *
	 */


	/**
	 * Creates transactions, but only if they don't exist yet.
	 */

	/**
	 * The account out is usually copied from the invoice and represents the debtors account which this payment line withdraws from.
	 *
	 */
	@Override
	void setAccountOut(@Nullable Account accountOut) {
		if (accountOut != null && !accountOut.isAsset()) {
			throw new IllegalStateException("Payment account out must be an asset account, and is usually 'trade debtors'.")
		}
		super.setAccountOut(accountOut)
	}

	/**
	 * @return linked payment record
	 */
	@Nonnull
	@API
	PaymentInterface getPayment() {
		return getPaymentIn()
	}

	@Nullable
	String getShortRecordDescription() {
		// not important on server
		return null
	}

	@Override
	void postPersist() {
		super.postPersist()
	}

	@Override
	void postUpdate() {
		super.postUpdate()
	}

	void setPayment(PaymentInterface payment) {
		setPaymentIn((PaymentIn) payment)
	}

	/**
	 * @return account linked to this PaymentInLine
	 */
	@Nonnull
	@API
	Account getAccount() {
		return getAccountOut()
	}

	void setAccount(AccountInterface account) {
		setAccountOut((Account) account)
	}

	void setInvoice(InvoiceInterface invoice) {
		super.setInvoice((Invoice) invoice)
	}

	/**
	 * Checks if async replication allowed on entity.
	 *
	 * @return isAsyncReplicationAllowed
	 */
	@Override
	boolean logicAllowsReplication() {
		return getPaymentIn() != null && getPaymentIn().logicAllowsReplication()
	}

	/**
	 * @return amount of money paid against this particular invoice
	 */
	@Nonnull
	@API
	@Override
	Money getAmount() {
		return super.getAmount()
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
	 * @return the date and time this record was modified
	 */
	@API
	@Override
	Date getModifiedOn() {
		return super.getModifiedOn()
	}



	/**
	 * @return
	 */
	@Nonnull
	@API
	@Override
	Account getAccountOut() {
		return super.getAccountOut()
	}

	/**
	 * @return linked invoice record
	 */
	@Nonnull
	@API
	@Override
	Invoice getInvoice() {
		return super.getInvoice()
	}

	/**
	 * @return linked payment record
	 */
	@Nonnull
	@API
	@Override
	PaymentIn getPaymentIn() {
		return super.getPaymentIn()
	}

	//TODO docs
	/**
	 * @return
	 */
	@Nonnull
	@API
	@Override
	Payslip getPayslip() {
		return super.getPayslip()
	}
}
