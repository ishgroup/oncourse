/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package ish.oncourse.server.util

import ish.common.types.DiscountType
import ish.math.Money
import ish.math.MoneyRounding
import ish.oncourse.server.cayenne.Discount

class DiscountUtils {

    static Money discountValue(Discount classDiscount, Money priceExTax, BigDecimal taxRate) {
        if (classDiscount == null ) {
            return  Money.ZERO
        }
        Money total = getDiscountedFee(classDiscount, priceExTax, taxRate)
        return priceExTax.subtract(total.divide(BigDecimal.ONE.add(taxRate)))
    }

    static Money getDiscountedFee(Discount discount, Money priceExTax, BigDecimal taxRate) {
        if (taxRate == null) {
            taxRate = new BigDecimal(0)
        }
        if (discount == null || priceExTax.isZero()) {
            return priceExTax.multiply(BigDecimal.ONE.add(taxRate))
        }

        Money discountAmount = Money.ZERO
        discountAmount = discountAmount.add(discountPriceValue(discount, priceExTax))

        if (discountAmount.compareTo(priceExTax) >= 0) {
            return  Money.ZERO
        }

        MoneyRounding rounding = discount.getRounding()

        Money total = priceExTax.subtract(discountAmount).multiply(BigDecimal.ONE.add(taxRate))
        return total.round(rounding)
    }

    private static Money discountPriceValue(Discount discount, Money price) {
        if (price.isZero()) {
            return price
        }
        Money discountValue

        if (discount.discountType == DiscountType.DOLLAR && discount.getDiscountDollar() != null) {
            discountValue = discount.getDiscountDollar()
        } else {
            BigDecimal discountRate = discount.getDiscountPercent()
            DiscountType discountType = discount.getDiscountType()

            if (discountType == null) {
                if (discountRate == null) {
                    discountValue = discount.getDiscountDollar()
                } else {
                    discountValue = price.multiply(discountRate)
                }
            } else {
                switch (discountType) {
                    case DiscountType.FEE_OVERRIDE:
                        discountValue = price.subtract(discount.getDiscountDollar())
                        break
                    case DiscountType.DOLLAR:
                        discountValue = discount.getDiscountDollar()
                        break
                    case DiscountType.PERCENT:
                        discountValue = price.multiply(discountRate)
                        break
                }
            }

            Money maximumDiscount = discount.getDiscountMax()
            if (Money.ZERO.isLessThan(maximumDiscount) && discountValue.compareTo(maximumDiscount) > 0) {
                discountValue = maximumDiscount
            } else {
                Money minimumDiscount = discount.getDiscountMin()
                if (Money.ZERO.isLessThan(minimumDiscount) && discountValue.compareTo(minimumDiscount) < 0) {
                    discountValue = minimumDiscount
                }
            }
        }


        if (price.compareTo(discountValue) < 0) {
            return price
        }
        return discountValue
    }
}
