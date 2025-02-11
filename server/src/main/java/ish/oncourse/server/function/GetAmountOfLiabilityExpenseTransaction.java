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

package ish.oncourse.server.function;

import ish.math.Money;
import ish.oncourse.server.cayenne.*;

/**
 * Created by anarut on 10/14/16.
 */
public class GetAmountOfLiabilityExpenseTransaction {

    private PaymentInLine paymentInLine;

    private GetAmountOfLiabilityExpenseTransaction() {

    }

    public static GetAmountOfLiabilityExpenseTransaction valueOf(PaymentInLine paymentInLine) {
        var function = new GetAmountOfLiabilityExpenseTransaction();
        function.paymentInLine = paymentInLine;
        return function;
    }

    public Money get() {
        var amountPaid = paymentInLine.getAmount();

        var voucher = paymentInLine.getPaymentIn().getVoucherPayments().get(0).getVoucher();
        var voucherProduct = voucher.getVoucherProduct();

        if (voucherProduct.getMaxCoursesRedemption() == null && voucherProduct.getPriceExTax() == null) {
            //it's a purchase value voucher and amount of 2nd transaction equals the 1st
            return amountPaid;
        } else {
            var allAmountPaid = voucher.getVoucherPaymentsIn().stream()
                    .map(VoucherPaymentIn::getPaymentIn)
                    .distinct()
                    .map(PaymentIn::getAmount)
                    .reduce(Money.ZERO(), Money::add);

            var allRedemptionValue = voucher.getInvoiceLine().getPriceEachExTax();
            var redemptionValueLeft = allRedemptionValue.add(amountPaid).subtract(allAmountPaid).max(Money.ZERO());

            return ((voucherProduct.getMaxCoursesRedemption() != null && voucherProduct.getMaxCoursesRedemption().equals(voucher.getRedeemedCourseCount())) ||
                    (allAmountPaid.equals(voucher.getValueOnPurchase()))) ? redemptionValueLeft : redemptionValueLeft.min(amountPaid);
        }
    }
}
