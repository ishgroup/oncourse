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

package ish.oncourse.server.accounting

import ish.common.types.AccountTransactionType
import ish.math.Money
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.InvoiceLine
import ish.oncourse.server.cayenne.PaymentInLine
import ish.oncourse.server.cayenne.PaymentOutLine

import java.time.LocalDate

class AccountTransactionDetail {

    Account primaryAccount, secondaryAccount
    Money amount
    AccountTransactionType tableName
    Long foreignRecordId
    LocalDate transactionDate
    String description = null

    private AccountTransactionDetail() {

    }

    static AccountTransactionDetail valueOf(InvoiceLine invoiceLine, Money amount, Account primaryAccount, Account secondaryAccount, LocalDate transactionDate) {
        valueOf(primaryAccount, secondaryAccount, amount, AccountTransactionType.INVOICE_LINE, invoiceLine.id, transactionDate)
    }

    static AccountTransactionDetail valueOf(PaymentInLine paymentInLine, Money amount, Account primaryAccount, Account secondaryAccount, LocalDate transactionDate) {
        valueOf(primaryAccount, secondaryAccount, amount, AccountTransactionType.PAYMENT_IN_LINE, paymentInLine.id, transactionDate)
    }

    static AccountTransactionDetail valueOf(PaymentOutLine paymentOutLine, Money amount, Account primaryAccount, Account secondaryAccount, LocalDate transactionDate) {
        valueOf(primaryAccount, secondaryAccount, amount, AccountTransactionType.PAYMENT_OUT_LINE, paymentOutLine.id, transactionDate)
    }

    static AccountTransactionDetail valueOf(Account primaryAccount, Account secondaryAccount, Money amount,
                                            AccountTransactionType tableName, Long foreignRecordId, LocalDate transactionDate) {
        AccountTransactionDetail details = new AccountTransactionDetail()
        details.primaryAccount = primaryAccount
        details.secondaryAccount = secondaryAccount
        details.amount = amount
        details.tableName = tableName
        details.foreignRecordId = foreignRecordId
        details.transactionDate = transactionDate
        details
    }

    Account getPrimaryAccount() {
        return primaryAccount
    }

    Account getSecondaryAccount() {
        return secondaryAccount
    }

    Money getAmount() {
        return amount
    }

    AccountTransactionType getTableName() {
        return tableName
    }

    Long getForeignRecordId() {
        return foreignRecordId
    }

    LocalDate getTransactionDate() {
        return transactionDate
    }

    String getDescription() {
        return description
    }

    void setDescription(String description) {
        this.description = description
    }

}
