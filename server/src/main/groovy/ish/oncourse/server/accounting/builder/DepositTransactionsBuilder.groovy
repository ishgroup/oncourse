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

import ish.oncourse.cayenne.PaymentLineInterface
import ish.oncourse.server.accounting.AccountTransactionDetail
import ish.oncourse.server.accounting.TransactionSettings
import ish.oncourse.server.cayenne.PaymentInLine
import ish.oncourse.server.cayenne.PaymentOutLine

import java.time.LocalDate

/**
 for deposited transactions transactions
 */
class DepositTransactionsBuilder implements TransactionsBuilder {

    private PaymentLineInterface line
    private LocalDate oldSettlementDate
    private LocalDate newSettlementDate

    List<AccountTransactionDetail> details = []

    private DepositTransactionsBuilder() {

    }

    static DepositTransactionsBuilder valueOf(PaymentLineInterface line, LocalDate oldSettlementDate) {
        valueOf(line, oldSettlementDate, null)
    }

    static DepositTransactionsBuilder valueOf(PaymentLineInterface line, LocalDate oldSettlementDate, LocalDate newSettlementDate) {
        DepositTransactionsBuilder builder = new DepositTransactionsBuilder()
        builder.line = line
        builder.oldSettlementDate = oldSettlementDate
        builder.newSettlementDate = newSettlementDate
        builder
    }

    @Override
    TransactionSettings build() {
        if (line instanceof PaymentInLine) {
            buildDetails(line)
        }

        if (line instanceof PaymentOutLine) {
            buildDetails(line)
        }

        TransactionSettings.valueOf(details)
    }

    private void buildDetails(PaymentInLine paymentInLine) {
        if (oldSettlementDate) {
            details.add(AccountTransactionDetail.valueOf(paymentInLine, paymentInLine.amount, paymentInLine.paymentIn.undepositedFundsAccount, paymentInLine.paymentIn.accountIn, oldSettlementDate))
        }

        if (newSettlementDate) {
            details.add(AccountTransactionDetail.valueOf(paymentInLine, paymentInLine.amount, paymentInLine.paymentIn.accountIn, paymentInLine.paymentIn.undepositedFundsAccount, newSettlementDate))
        }
    }

    private void buildDetails(PaymentOutLine paymentOutLine) {
        if (oldSettlementDate) {
            details.add(AccountTransactionDetail.valueOf(paymentOutLine, paymentOutLine.amount, paymentOutLine.paymentOut.accountOut, paymentOutLine.paymentOut.undepositedFundsAccount, oldSettlementDate))
        }

        if (newSettlementDate) {
            details.add(AccountTransactionDetail.valueOf(paymentOutLine, paymentOutLine.amount, paymentOutLine.paymentOut.undepositedFundsAccount, paymentOutLine.paymentOut.accountOut, newSettlementDate))
        }
    }
}
