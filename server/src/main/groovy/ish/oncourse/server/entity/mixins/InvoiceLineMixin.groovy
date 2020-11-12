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
import ish.oncourse.entity.services.InvoiceLineService
import ish.oncourse.server.cayenne.InvoiceLine
import static ish.oncourse.server.entity.mixins.MixinHelper.getService

class InvoiceLineMixin {

    /**
     * Returns names of all discounts applied to invoiceLine in single string delimited by "/".
     *
     * @param self
     * @return
     */
    //TODO: this kind of display formatting belongs in the report or other output
	@Deprecated
	static getDiscountNames(InvoiceLine self) {
		return getService(InvoiceLineService).getDiscountNames(self)
	}

    /**
     * Is it possible for tax to be applied to this account? For now this is a simplistic test: liability and asset
     * accounts cannot have tax applied.
     *
     * @param self
     * @return true if tax could be applied
     */
	@API
	static isTaxableAccount(InvoiceLine self) {
		return !self.account.liability && !self.account.asset
	}
}
