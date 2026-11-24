/*
 * Copyright ish group pty ltd 2025.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Discount, Tax } from '@api/model';
import { Decimal, Numeric } from 'decimal.js-light';
import {
  getDiscountAmountByFeeIncTaxAndDiscount,
  getDiscountAmountExTax,
  getDiscountedFee
} from '../../containers/entities/discounts/utils';

export const bankRounding = (fee: Numeric) => new Decimal(fee).toDecimalPlaces(2, Decimal.ROUND_HALF_EVEN).toNumber();

export const getTotalByFeeExTax = (taxRate: number, feeExTax: number): number => bankRounding(new Decimal(feeExTax || 0).mul(new Decimal(1).plus(taxRate || 0)));

export const getFeeExTaxByFeeIncTax = (taxRate: number, feeIncTax: number): number => bankRounding(new Decimal(feeIncTax || 0).div(new Decimal(1).plus(taxRate || 0)));

export const getTaxAmountByFeeExTax = (taxRate: number, feeExTax: number): number => taxRate === 0 ? 0 : bankRounding(new Decimal(feeExTax || 0).mul(new Decimal(taxRate || 1)));

export const getCurrentTax = (taxes: Tax[], id: number): Tax => taxes.find(t => t.id === id);

/**
 * Total and tax of invoice line calculated from price and discount, when there is no discount rounding to apply:
 * total is (price - discount) * (1 + rate) rounded half even, tax is the rest
 */
export const getInvoiceLineTotalAndTax = (
  priceEachExTax: number,
  discountEachExTax: number,
  taxRate: number
): {
  total: number;
  taxEach: number;
} => {
  const afterDiscount = new Decimal(priceEachExTax || 0).sub(discountEachExTax || 0);
  const total = bankRounding(afterDiscount.mul(new Decimal(taxRate || 0).add(1)));

  return {
    total,
    taxEach: bankRounding(new Decimal(total).sub(afterDiscount))
  };
};

export function getTotalAndDeductionsByPrice(
  priceEachExTax: number,
  taxRate: number,
  discount?: Discount | number
): {
  total: number;
  discountEach: number;
  taxEach: number;
}  {
  if (!discount || typeof discount === 'number') {
    const discountEach = bankRounding((discount as number) || 0);

    return {
      discountEach,
      ...getInvoiceLineTotalAndTax(priceEachExTax, discountEach, taxRate)
    };
  }

  // the same as checkout (DiscountUtils.applyDiscounts): total is the discounted fee inc tax rounded by discount rounding,
  // discount is worked back from it and tax takes the rounding difference, server keeps it (InvoiceUtil.calculateTaxEachForManualInvoiceLine)
  const discountEach = bankRounding(getDiscountAmountExTax(discount, taxRate, priceEachExTax));
  const total = bankRounding(getDiscountedFee(discount, taxRate, priceEachExTax));

  return {
    total,
    discountEach,
    taxEach: bankRounding(new Decimal(total).sub(new Decimal(priceEachExTax || 0).sub(discountEach)))
  };
}

/**
 * Price, discount and tax for a total typed in by user, the total is kept.
 * When current price with its discount gives the typed total within a cent, price and discount are kept and tax takes the difference,
 * otherwise price is worked back from the total.
 * Tax can differ from (price - discount) * rate by up to a cent, server keeps it (InvoiceUtil.calculateTaxEachForManualInvoiceLine)
 */
export function getPriceAndDeductionsByTotal(
  total: number,
  taxRate: number,
  discount?: Discount | number,
  currentPriceEachExTax?: number
): {
  priceEachExTax: number,
  discountAmount: number,
  taxEach: number
} {
  const totalDec = new Decimal(total);

  if (currentPriceEachExTax) {
    const { discountEach } = getTotalAndDeductionsByPrice(currentPriceEachExTax, taxRate, discount);

    // compare with the total calculated from price and discount, so that tax stays within a cent of (price - discount) * rate
    if (totalDec.sub(getInvoiceLineTotalAndTax(currentPriceEachExTax, discountEach, taxRate).total).abs().lte(0.01)) {
      return {
        taxEach: bankRounding(totalDec.sub(new Decimal(currentPriceEachExTax).sub(discountEach))),
        discountAmount: discountEach,
        priceEachExTax: currentPriceEachExTax
      };
    }
  }

  const taxRateDec = new Decimal(taxRate || 0);
  const afterDiscount = totalDec.div(taxRateDec.add(1));
  const discountAmount = typeof discount === 'number'
    ? discount
    : discount
      ? getDiscountAmountByFeeIncTaxAndDiscount(discount, afterDiscount)
      : 0;
  const priceEachExTax = discountAmount
    ? afterDiscount.plus(discountAmount)
    : afterDiscount;
  const priceRounded = bankRounding(priceEachExTax);
  const discountRounded = bankRounding(discountAmount);

  // round price and discount first and let tax take the rest,
  // so that the rounding difference is counted once and price minus discount plus tax always equals total
  return {
    taxEach: bankRounding(totalDec.sub(new Decimal(priceRounded).sub(discountRounded))),
    discountAmount: discountRounded,
    priceEachExTax: priceRounded
  };
}
