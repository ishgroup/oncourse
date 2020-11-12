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

package ish.oncourse.server.entity.mixins

import ish.oncourse.API
import ish.oncourse.server.cayenne.PaymentIn

//TODO docs
class PaymentInMixin {


	static final String DISPLAY_STATYS = "statusString"
	/**
	 * Returns the account this PaymentIn is payed in to
	 * @param self
	 * @return account associated with this payment
	 */
	@API
	static getAccount(PaymentIn self) {
		return self.accountIn
	}

	/**
	 * Returns the PaymentStatus of this PaymentIn
	 *
	 * @param self
	 * @return PaymentStatus as a stringz`
	 */
	@API
	static getStatusString(PaymentIn self) {
		return self.getStatusString()
	}
}
