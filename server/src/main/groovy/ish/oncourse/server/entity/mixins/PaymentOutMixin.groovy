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
import ish.oncourse.server.cayenne.PaymentOut

class PaymentOutMixin {

	/**
	 * @return account linked with this PaymentOut
	 */
	@API
	static getAccount(PaymentOut self) {
		return self.accountOut
	}

	/**
	 * @return the display name of the PaymentOut status
	 */
	@API
	static getStatusString(PaymentOut self) {
		return self.getStatusString()
	}
}
