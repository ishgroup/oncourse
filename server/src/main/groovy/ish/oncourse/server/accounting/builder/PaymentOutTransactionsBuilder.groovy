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

package ish.oncourse.server.accounting.builder

import ish.oncourse.server.accounting.AccountTransactionDetail
import ish.oncourse.server.accounting.TransactionSettings
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.PaymentOutLine

/**
 for initial paymentOut transactions
 */
class PaymentOutTransactionsBuilder implements TransactionsBuilder {

    private PaymentOutLine paymentOutLine

    private PaymentOutTransactionsBuilder() {

    }

    static PaymentOutTransactionsBuilder valueOf(PaymentOutLine paymentOutLine) {
        PaymentOutTransactionsBuilder builder = new PaymentOutTransactionsBuilder()
        builder.paymentOutLine = paymentOutLine
        builder
    }

    @Override
    TransactionSettings build() {
        Account primaryAccount = paymentOutLine.invoice.debtorsAccount
        Account secondaryAccount = (paymentOutLine.paymentOut.paymentMethod.bankedAutomatically && paymentOutLine.paymentOut.banking) ? paymentOutLine.paymentOut.accountOut : paymentOutLine.paymentOut.undepositedFundsAccount

        TransactionSettings.valueOf(AccountTransactionDetail.valueOf(paymentOutLine, paymentOutLine.amount, primaryAccount, secondaryAccount, paymentOutLine.paymentOut.paymentDate))
                .initialTransaction()
    }
}
