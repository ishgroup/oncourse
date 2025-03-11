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
import ish.oncourse.server.api.v1.function.BankingFunctions
import ish.oncourse.server.cayenne.Banking
import ish.oncourse.server.cayenne.PaymentIn
import ish.oncourse.server.cayenne.PaymentOut
import org.apache.cayenne.query.ObjectSelect

class BankingMixin {

	public static String RECONCILED_STATUS = "reconciledStatus"
	public static String TOTAL = "total"

	static String getReconciledStatus(Banking self) {
		BankingFunctions.getReconciledStatus(self).toString()
	}

	static Money getTotal(Banking self) {
		List<Money> allPaymentIns = ObjectSelect.columnQuery(Banking, Banking.PAYMENTS_IN.dot(PaymentIn.AMOUNT))
				.suppressDistinct()
				.where(Banking.ID.eq(self.id))
				.select(self.context)

		List<Money> allPaymentOuts = ObjectSelect.columnQuery(Banking, Banking.PAYMENTS_OUT.dot(PaymentOut.AMOUNT))
				.suppressDistinct()
				.where(Banking.ID.eq(self.id))
				.select(self.context)

		def paymentInSum = Money.ZERO
		allPaymentIns.each { paymentInSum = paymentInSum.add(it) }

		def paymentsOutSum = Money.ZERO
		allPaymentOuts.each { paymentsOutSum = paymentsOutSum.add(it) }

		return paymentInSum.subtract(paymentsOutSum)
	}
}
