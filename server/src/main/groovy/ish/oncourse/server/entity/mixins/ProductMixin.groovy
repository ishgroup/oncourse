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

import ish.math.Money
import ish.oncourse.server.cayenne.Product
import ish.util.MoneyUtil

class ProductMixin {

    static Money getPrice_with_tax(Product self) {
        if (self.getTax() != null) {
            return MoneyUtil.getPriceIncTaxRounded(self.getPriceExTax(), self.getTax().getRate(), self.getTaxAdjustment(), true)
        }
        return MoneyUtil.getPriceIncTax(self.getPriceExTax(), null, self.getTaxAdjustment())
    }
}
