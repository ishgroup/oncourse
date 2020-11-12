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

class TransactionSettings {

    List<AccountTransactionDetail> details

    private boolean isInitialTransaction = false

    private TransactionSettings() {

    }

    static TransactionSettings valueOf(List<AccountTransactionDetail> details) {
        TransactionSettings settings = new TransactionSettings()
        settings.details = details
        settings
    }

    static TransactionSettings valueOf(AccountTransactionDetail... details) {
        TransactionSettings settings = new TransactionSettings()
        settings.details = details.toList()
        settings
    }

    TransactionSettings initialTransaction() {
        isInitialTransaction = true
        this
    }

    List<AccountTransactionDetail> getDetails() {
        return details
    }

    boolean getIsInitialTransaction() {
        return isInitialTransaction
    }
}
