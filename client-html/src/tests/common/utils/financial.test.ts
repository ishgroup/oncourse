/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Discount } from '@api/model';
import { Decimal } from 'decimal.js-light';
import { getPriceAndDeductionsByTotal, getTotalAndDeductionsByPrice } from '../../../js/common/utils/financial';
import { getRoundingByType } from '../../../js/containers/entities/discounts/utils';

const feeOverride: Discount = {
  name: 'Fee override',
  discountType: 'Fee override',
  rounding: 'Nearest dollar',
  discountValue: 454.54
};

describe('getTotalAndDeductionsByPrice', () => {
  it('calculates ex tax discount and tax for Fee override so that price minus discount plus tax equals total', () => {
    const { total, discountEach, taxEach } = getTotalAndDeductionsByPrice(995.45, 0.1, feeOverride);

    expect(discountEach).toBe(540.9);
    expect(taxEach).toBe(45.45);
    expect(total).toBe(500);
  });

  it('calculates negative ex tax discount when Fee override is greater than price', () => {
    const { total, discountEach, taxEach } = getTotalAndDeductionsByPrice(100, 0.1, feeOverride);

    expect(discountEach).toBe(-354.55);
    expect(taxEach).toBe(45.45);
    expect(total).toBe(500);
  });

  it('calculates Fee override without tax', () => {
    const { total, discountEach, taxEach } = getTotalAndDeductionsByPrice(995.45, 0, feeOverride);

    expect(discountEach).toBe(540.45);
    expect(taxEach).toBe(0);
    expect(total).toBe(455);
  });

  it('keeps Dollar discount calculation unchanged', () => {
    const { total, discountEach, taxEach } = getTotalAndDeductionsByPrice(100, 0.1, { discountType: 'Dollar', discountValue: 10, rounding: 'No Rounding' });

    expect(discountEach).toBe(10);
    expect(taxEach).toBe(9);
    expect(total).toBe(99);
  });

  it('keeps price and puts rounding into tax when Fee override is removed', () => {
    const { total, discountEach, taxEach } = getTotalAndDeductionsByPrice(995.45, 0.1, null);

    expect(discountEach).toBe(0);
    expect(taxEach).toBe(99.55);
    expect(total).toBe(1095);
  });
});

describe('getPriceAndDeductionsByTotal', () => {
  it('does not move Fee override difference into tax when total is edited', () => {
    const { priceEachExTax, discountAmount, taxEach } = getPriceAndDeductionsByTotal(600, 0.1, feeOverride);

    expect(discountAmount).toBe(0);
    expect(priceEachExTax).toBe(545.45);
    expect(taxEach).toBe(54.55);
  });

  it('splits rounded Fee override total for line without price', () => {
    const { priceEachExTax, discountAmount, taxEach } = getPriceAndDeductionsByTotal(500, 0.1, feeOverride);

    expect(discountAmount).toBe(0);
    expect(priceEachExTax).toBe(454.55);
    expect(taxEach).toBe(45.45);
  });

  it('keeps zero tax for Fee override when tax rate is 0', () => {
    const { priceEachExTax, discountAmount, taxEach } = getPriceAndDeductionsByTotal(500, 0, feeOverride);

    expect(discountAmount).toBe(0);
    expect(priceEachExTax).toBe(500);
    expect(taxEach).toBe(0);
  });

  it('keeps Dollar discount calculation by total unchanged', () => {
    const { priceEachExTax, discountAmount, taxEach } = getPriceAndDeductionsByTotal(99, 0.1, { discountType: 'Dollar', discountValue: 10, rounding: 'No Rounding' });

    expect(discountAmount).toBe(10);
    expect(priceEachExTax).toBe(100);
    expect(taxEach).toBe(9);
  });
});

describe('Fee override total for line without price', () => {
  it('applies discount rounding so that total matches line with price', () => {
    const total = getRoundingByType(feeOverride.rounding, new Decimal(feeOverride.discountValue).mul(1.1));

    expect(total).toBe(500);
    expect(total).toBe(getTotalAndDeductionsByPrice(995.45, 0.1, feeOverride).total);
  });
});
