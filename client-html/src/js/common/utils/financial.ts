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
  getDiscountAmountExTax
} from '../../containers/entities/discounts/utils';

export const bankRounding = (fee: Numeric) => new Decimal(fee).toDecimalPlaces(2, Decimal.ROUND_HALF_EVEN).toNumber();

export const getTotalByFeeExTax = (taxRate: number, feeExTax: number): number => bankRounding(new Decimal(feeExTax || 0).mul(new Decimal(1).plus(taxRate || 0)));

export const getFeeExTaxByFeeIncTax = (taxRate: number, feeIncTax: number): number => bankRounding(new Decimal(feeIncTax || 0).div(new Decimal(1).plus(taxRate || 0)));

export const getTaxAmountByFeeExTax = (taxRate: number, feeExTax: number): number => bankRounding(new Decimal(feeExTax || 0).mul(new Decimal(taxRate || 1)));

export const getCurrentTax = (taxes: Tax[], id: number): Tax => taxes.find(t => t.id === id);

export const getPriceAndDeductionsByTotal = (
  total: number,
  taxRate: number,
  discount?: Discount | number
): {
  priceEachExTax: number,
  discountAmount: number,
  taxEach: number
} => {
  const totalDec = new Decimal(total);
  const taxRateDec = new Decimal(taxRate || 0);
  const afterDiscount = totalDec.div(taxRateDec.add(1));
  const discountAmount = typeof discount === 'number'
    ? discount
    : discount
      ? getDiscountAmountByFeeIncTaxAndDiscount(discount, afterDiscount)
      : 0;

  return {
    taxEach: bankRounding(afterDiscount.mul(taxRateDec)),
    discountAmount: bankRounding(discountAmount),
    priceEachExTax: bankRounding(discountAmount
      ? afterDiscount.plus(discountAmount)
      : afterDiscount)
  };
};

export const getTotalAndDeductionsByPrice = (
  priceEachExTax: number,
  taxRate: number,
  discount?: Discount | number
): {
  total: number;
  discountEach: number;
  taxEach: number;
} => {
  const base = new Decimal(priceEachExTax);
  const taxRateDec = new Decimal(taxRate || 0);

  const discountAmount = (typeof discount === 'number'
    ? discount
    : discount && getDiscountAmountExTax(discount, taxRate, priceEachExTax)) || 0;
  
  const afterDiscount = base.sub(discountAmount);
  const tax = afterDiscount.mul(taxRateDec);
  const total = typeof discount !== 'number' && discount?.discountType === 'Fee override' 
    ? afterDiscount 
    : afterDiscount.add(tax);

  return {
    total: bankRounding(total),
    discountEach: bankRounding(discountAmount),
    taxEach: bankRounding(tax)
  };
};