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

export function getPriceAndDeductionsByTotal(
  total: number,
  taxRate: number,
  discount?: Discount | number
): {
  priceEachExTax: number,
  discountAmount: number,
  taxEach: number
} {
  const totalDec = new Decimal(total);
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

export function getTotalAndDeductionsByPrice(
  priceEachExTax: number,
  taxRate: number,
  discount?: Discount | number
): {
  total: number;
  discountEach: number;
  taxEach: number;
}  {
  const base = new Decimal(priceEachExTax);
  const taxMul = new Decimal(taxRate || 0).add(1);
  const discountEntity = typeof discount === 'number' ? null : discount;

  const discountAmount = discountEntity
    ? getDiscountAmountExTax(discountEntity, taxRate, priceEachExTax)
    : (discount as number) || 0;

  // round discount and total first and let tax take the rest,
  // so that price minus discount plus tax always equals total
  const discountEach = bankRounding(discountAmount);
  const afterDiscount = base.sub(discountEach);

  // discount rounding is applied to the total inc tax, the same way as on server (DiscountUtils.applyDiscounts)
  const total = discountEntity?.rounding && discountEntity.rounding !== 'No Rounding'
    ? bankRounding(getDiscountedFee(discountEntity, taxRate, priceEachExTax))
    : bankRounding(afterDiscount.mul(taxMul));

  return {
    total,
    discountEach,
    taxEach: bankRounding(new Decimal(total).sub(afterDiscount))
  };
}
