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

import ish.common.types.PaymentType
import ish.math.Money
import ish.oncourse.server.accounting.AccountTransactionDetail
import ish.oncourse.server.accounting.TransactionSettings
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.PaymentInLine
import ish.oncourse.server.function.GetAmountOfLiabilityExpenseTransaction
import ish.util.PaymentMethodUtil

import java.time.LocalDate

/**
 for initial paymentIn transactions
 */
class PaymentInTransactionsBuilder implements TransactionsBuilder {

    private PaymentInLine paymentInLine
    private Closure<Money> getLiabilityExpenseAmount
    private LocalDate date
    private Account underpaymentAccount
    private PaymentInTransactionsBuilder() {

    }

    static PaymentInTransactionsBuilder valueOf(PaymentInLine paymentInLine, Account voucherExpense) {
        valueOf(paymentInLine, voucherExpense, { GetAmountOfLiabilityExpenseTransaction.valueOf(paymentInLine).get().negate() }, null)
    }

    static PaymentInTransactionsBuilder valueOf(PaymentInLine paymentInLine, Account voucherExpense, LocalDate date) {
        valueOf(paymentInLine, voucherExpense, { GetAmountOfLiabilityExpenseTransaction.valueOf(paymentInLine).get().negate() }, date)
    }

    static PaymentInTransactionsBuilder valueOf(PaymentInLine paymentInLine, Account voucherExpense, Closure<Money> getLiabilityExpenseAmount) {
        valueOf(paymentInLine, voucherExpense, getLiabilityExpenseAmount, null)
    }

    static PaymentInTransactionsBuilder valueOf(PaymentInLine paymentInLine, Account voucherExpense, Closure<Money> getLiabilityExpenseAmount, LocalDate date) {
        PaymentInTransactionsBuilder builder = new PaymentInTransactionsBuilder()
        builder.paymentInLine = paymentInLine
        if (PaymentType.VOUCHER  == paymentInLine.paymentIn.paymentMethod.type && !paymentInLine.paymentIn.voucherPayments.empty) {
            builder.underpaymentAccount = paymentInLine.paymentIn?.voucherPayments[0].voucher.voucherProduct.underpaymentAccount ?:voucherExpense
        }
        builder.getLiabilityExpenseAmount = getLiabilityExpenseAmount
        builder.date = date
        builder
    }


    @Override
    TransactionSettings build() {

        LocalDate transactionDate = date ?: paymentInLine.paymentIn.paymentDate

        Account primaryAccount = (paymentInLine.paymentIn.banking || PaymentMethodUtil.SYSTEM_TYPES.contains(paymentInLine.paymentIn.paymentMethod.type)) ?
                paymentInLine.paymentIn.accountIn : paymentInLine.paymentIn.undepositedFundsAccount

        Account secondaryAccount = paymentInLine.invoice.debtorsAccount

        if (PaymentType.VOUCHER == paymentInLine.paymentIn.paymentMethod.type) {
            Money amountPaid = paymentInLine.amount
            Money amountOf2ndTransaction = getLiabilityExpenseAmount.call()
            
            return TransactionSettings.valueOf(
                    AccountTransactionDetail.valueOf(paymentInLine, amountPaid, underpaymentAccount , secondaryAccount, transactionDate),
                    AccountTransactionDetail.valueOf(paymentInLine, amountOf2ndTransaction, underpaymentAccount, primaryAccount, transactionDate))
                    .initialTransaction()

        } else {
            return TransactionSettings.valueOf(
                    AccountTransactionDetail.valueOf(paymentInLine, paymentInLine.amount, primaryAccount, secondaryAccount, transactionDate))
                    .initialTransaction()
        }
    }
}
