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
import ish.oncourse.cayenne.AccountInterface
import ish.oncourse.cayenne.InvoiceInterface
import ish.oncourse.cayenne.PaymentInterface
import ish.oncourse.cayenne.PaymentOutLineInterface
import ish.oncourse.server.cayenne.glue._PaymentOutLine
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import javax.annotation.Nonnull
import java.util.Date

/**
 * Payment line links payment with one or multiple invoices which are getting paid for.
 */
@API
class PaymentOutLine extends _PaymentOutLine implements PaymentOutLineInterface {

	/**
	 *
	 */

	private static final Logger logger = LogManager.getLogger()

	@Override
	void postPersist() {
		super.postPersist()
	}

	@Override
	void postUpdate() {
		super.postUpdate()
	}

	/**
	 * The account in is copied from Invoice. It cannot be related to asset account
	 *
	 */
	@Override
	void setAccountIn(@Nonnull Account accountOut) {
		if (!accountOut.isAsset()) {
			throw new IllegalStateException("Acount in cannot be linked to Asset account")
		}
		super.setAccountIn(accountOut)
	}

	/**
	 * @return linked payment record
	 */
	@Nonnull
	@API
	PaymentInterface getPayment() {
		return getPaymentOut()
	}

	String getShortRecordDescription() {
		// not important on server
		return null
	}

	void setInvoice(InvoiceInterface invoice) {
		super.setInvoice((Invoice) invoice)
	}

	void setPayment(PaymentInterface payment) {
		setPaymentOut((PaymentOut) payment)
	}

	/**
	 * @return
	 */
	@Nonnull
	@Deprecated
	AccountInterface getAccount() {
		return getAccountIn()
	}

	void setAccount(AccountInterface account) {
		setAccountIn((Account) account)
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
	 * @return account linked to this invoice line
	 */
	@Nonnull
	@API
	@Override
	Account getAccountIn() {
		return super.getAccountIn()
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
	PaymentOut getPaymentOut() {
		return super.getPaymentOut()
	}
}
