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

import ish.common.types.PaymentType
import ish.oncourse.API
import ish.oncourse.cayenne.PaymentMethodInterface
import ish.oncourse.server.cayenne.glue._PaymentMethod

import javax.annotation.Nonnull

/**
 * Payment methods are user definable ways that a payment might be taken. Typically you'd have cash, credit card,
 * cheque, EFT as the basic payment methods. But you might add more such as "internal transfer" or perhaps different
 * types of cheques to be handled and banked separately.
 */
@API
class PaymentMethod extends _PaymentMethod implements PaymentMethodInterface {



	@Override
	void onEntityCreation() {
		if (getActive() == null) {
			setActive(true)
		}
		if (getBankedAutomatically() == null) {
			setBankedAutomatically(false)
		}
		if (getReconcilable() == null) {
			setReconcilable(true)
		}
		super.onEntityCreation()
	}

	/**
	 * Each payment method is linked to an asset account into which the payment is deposited.
	 *
	 * @return account linked with this payment method
	 */
	@Nonnull
	@API
	@Override
	Account getAccount() {
		return super.getAccount()
	}

	/**
	 * A payment method which has already been used cannot be deleted, but it can be deactivated so
	 * it can no longer be used for future payments.
	 *
	 * @return true if the method is active
	 */
	@Nonnull
	@API
	@Override
	Boolean getActive() {
		return super.getActive()
	}

	/**
	 * If set, this payment will be marked as banked immediately upon creation.
	 * This is useful for payments which are processed in real time, such as credit cards.
	 *
	 * @return true is this method banks automatically
	 */
	@Nonnull
	@API
	@Override
	Boolean getBankedAutomatically() {
		return super.getBankedAutomatically()
	}

	/**
	 * If set, this payment will be marked as reconciled immediately upon creation.
	 *
	 * @return true if reconcilable on creation
	 */
	@Nonnull
	@API
	@Override
	Boolean getReconcilable() {
		return super.getReconcilable()
	}

	/**
	 * Return the special internal payment type. These payment types invoke special actions, such as opening a credit card
	 * processing window in a browser.
	 *
	 * @return internal payment type
	 */
	@Nonnull
	@API
	@Override
	PaymentType getType() {
		return super.getType()
	}

	/**
	 * Each payment method can have an arbitrary name set.
	 *
	 * @return name of this payment method
	 */
	@Nonnull
	@API
	@Override
	String getName() {
		return super.getName()
	}

}



