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

package ish.request;

import ish.math.Money;

import java.io.Serializable;
import java.time.LocalDate;

public class AccountTransactionRequest implements Serializable {

    private Money amount;
    private Long primaryAccountId;
    private Long secondaryAccountId;
    private LocalDate transactionDate;

    private AccountTransactionRequest() {

    }

    public static AccountTransactionRequest valueOf(Money amount, Long primaryAccountId, Long secondaryAccountId, LocalDate transactionDate) {
        AccountTransactionRequest request = new AccountTransactionRequest();
        request.amount = amount;
        request.primaryAccountId = primaryAccountId;
        request.secondaryAccountId = secondaryAccountId;
        request.transactionDate = transactionDate;
        return request;
    }

    public Money getAmount() {
        return amount;
    }

    public Long getPrimaryAccountId() {
        return primaryAccountId;
    }

    public Long getSecondaryAccountId() {
        return secondaryAccountId;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }
}
