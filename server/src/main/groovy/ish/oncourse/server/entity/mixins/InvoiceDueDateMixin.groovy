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
import ish.oncourse.API
import ish.oncourse.server.cayenne.InvoiceDueDate

class InvoiceDueDateMixin {

    /**
     * @param self
     * @return amount that still unpaid for the associate invoice
     */
    @API
    static getUnpaidAmount(InvoiceDueDate self) {
        Money amountPaid = self.invoice.amountPaid

        Money amountToBePaid = self.invoice.invoiceDueDates
                .findAll { it -> it.dueDate <= self.dueDate }
                .inject(Money.ZERO()) { total, it -> it.amount.add((Money) total) }

        amountToBePaid.subtract(amountPaid).max(Money.ZERO()).min(self.amount)
    }
}
