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

import ish.math.Money
import ish.oncourse.server.accounting.AccountTransactionDetail
import ish.oncourse.server.accounting.TransactionSettings
import ish.oncourse.server.cayenne.Account
import ish.oncourse.server.cayenne.Voucher
import ish.util.VoucherUtil

import java.time.LocalDate

class VoucherExpiryTransactionsBuilder implements TransactionsBuilder {

    private Voucher voucher
    private Account vouchersExpiredAccount
    private LocalDate transactionDate

    private VoucherExpiryTransactionsBuilder() {

    }

    static VoucherExpiryTransactionsBuilder valueOf(Voucher voucher, Account vouchersExpiredAccount, LocalDate transactionDate) {
        VoucherExpiryTransactionsBuilder builder = new VoucherExpiryTransactionsBuilder()
        builder.voucher = voucher
        builder.vouchersExpiredAccount = vouchersExpiredAccount
        builder.transactionDate = transactionDate
        builder
    }

    @Override
    TransactionSettings build() {
        Account voucherLiabilityAccount = voucher.voucherProduct.liabilityAccount
        Money voucherPrice = voucher.invoiceLine.finalPriceToPayExTax

        Money remainingLiability

        if (voucher.voucherProduct.maxCoursesRedemption) {
            int remainingEnrolments = voucher.voucherProduct.maxCoursesRedemption - voucher.redeemedCourseCount
            remainingLiability = VoucherUtil.calculateCourseVoucherRemainingLiability(
                    voucherPrice, remainingEnrolments, voucher.voucherProduct.maxCoursesRedemption)
        } else {
            remainingLiability = VoucherUtil.calculateMoneyVoucherRemainingLiability(
                    voucher.redemptionValue, voucherPrice, voucher.valueOnPurchase)
        }

        TransactionSettings.valueOf(AccountTransactionDetail.valueOf(voucher.invoiceLine, remainingLiability, vouchersExpiredAccount, voucherLiabilityAccount, transactionDate))
    }
}
