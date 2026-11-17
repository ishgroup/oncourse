/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Discount } from '@api/model';
import { Decimal } from 'decimal.js-light';
import { getPriceAndDeductionsByTotal, getTotalAndDeductionsByPrice } from '../../../js/common/utils/financial';
import { getDiscountAmountExTax, getRoundingByType } from '../../../js/containers/entities/discounts/utils';

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

  it('keeps total equal to rounded Fee override at tax rates other than 10%', () => {
    const discount: Discount = { discountType: 'Fee override', rounding: 'Nearest dollar', discountValue: 9.57 };
    const { total, discountEach, taxEach } = getTotalAndDeductionsByPrice(2000, 0.15, discount);

    expect(total).toBe(11);
    expect(discountEach).toBe(1990.43);
    expect(taxEach).toBe(1.43);
  });

  it.each([0, 0.05, 0.1, 0.125, 0.15, 0.2, 0.25])(
    'keeps total equal to rounded Fee override and price minus discount plus tax equal to total for tax rate %s',
    taxRate => {
      const taxMul = new Decimal(1).add(taxRate);

      for (let fee = 1; fee < 1000; fee++) {
        const discount: Discount = {
          discountType: 'Fee override',
          rounding: 'Nearest dollar',
          discountValue: new Decimal(fee).div(taxMul).toDecimalPlaces(2).toNumber()
        };
        const expectedTotal = getRoundingByType(discount.rounding, new Decimal(discount.discountValue).mul(taxMul));

        [0, 1, 99.99, 2000].forEach(price => {
          const { total, discountEach, taxEach } = getTotalAndDeductionsByPrice(price, taxRate, discount);

          expect(total).toBe(expectedTotal);
          expect(new Decimal(price).sub(discountEach).add(taxEach).toNumber()).toBe(total);
        });
      }
    }
  );

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

  it('splits rounded Fee override total for line without price at tax rates other than 10%', () => {
    const { priceEachExTax, discountAmount, taxEach } = getPriceAndDeductionsByTotal(11, 0.15, feeOverride);

    expect(discountAmount).toBe(0);
    expect(priceEachExTax).toBe(9.57);
    expect(taxEach).toBe(1.43);
  });

  it('puts price rounding into tax once when total is typed in', () => {
    const { priceEachExTax, discountAmount, taxEach } = getPriceAndDeductionsByTotal(100.15, 0.1);

    expect(discountAmount).toBe(0);
    expect(priceEachExTax).toBe(91.05);
    expect(taxEach).toBe(9.1);
  });

  const discountsByTotal: [string, Discount | number][] = [
    ['no discount', null],
    ['number discount', 3.33],
    ['Fee override', feeOverride],
    ['Dollar', { discountType: 'Dollar', discountValue: 10, rounding: 'No Rounding' }],
    ['Percent', { discountType: 'Percent', discountPercent: 0.15, rounding: 'No Rounding' }]
  ];

  describe.each(discountsByTotal)('with %s', (_, discount) => {
    it.each([0, 0.05, 0.1, 0.125, 0.15, 0.2, 0.25])(
      'keeps price minus discount plus tax equal to total for tax rate %s',
      taxRate => {
        const totals: number[] = [];
        for (let cents = 1; cents <= 10000; cents++) totals.push(cents / 100);
        for (let dollars = 101; dollars < 5000; dollars++) totals.push(dollars);

        totals.forEach(total => {
          const { priceEachExTax, discountAmount, taxEach } = getPriceAndDeductionsByTotal(total, taxRate, discount);

          expect(new Decimal(priceEachExTax).sub(discountAmount).add(taxEach).toNumber()).toBe(total);
        });
      }
    );
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

describe('Fee override with discount min/max', () => {
  it('limits ex tax discount by discountMax and rounds total by discount rounding', () => {
    const { total, discountEach, taxEach } = getTotalAndDeductionsByPrice(1000, 0.1, { ...feeOverride, discountMax: 100 });

    expect(discountEach).toBe(100);
    expect(total).toBe(990);
    expect(taxEach).toBe(90);
  });

  it('limits ex tax discount by discountMin and rounds total by discount rounding', () => {
    const { total, discountEach, taxEach } = getTotalAndDeductionsByPrice(1000, 0.1, { ...feeOverride, discountMin: 600 });

    expect(discountEach).toBe(600);
    expect(total).toBe(440);
    expect(taxEach).toBe(40);
  });

  it('keeps total equal to rounded Fee override when discount is within min/max', () => {
    const limited = getTotalAndDeductionsByPrice(1000, 0.1, { ...feeOverride, discountMin: 100, discountMax: 600 });

    expect(limited).toEqual(getTotalAndDeductionsByPrice(1000, 0.1, feeOverride));
    expect(limited).toEqual({ total: 500, discountEach: 545.45, taxEach: 45.45 });
  });

  it.each([0, 0.1, 0.15])('never exceeds min/max and keeps price minus discount plus tax equal to total for tax rate %s', taxRate => {
    const limits: Pick<Discount, 'discountMin' | 'discountMax'>[] = [{ discountMax: 100 }, { discountMin: 600 }, { discountMin: 50, discountMax: 300 }];

    limits.forEach(limit => [1, 11, 99, 500, 999].forEach(fee => [100, 1000, 2000].forEach(price => {
      const discount: Discount = {
        ...feeOverride,
        ...limit,
        discountValue: new Decimal(fee).div(1 + taxRate).toDecimalPlaces(2).toNumber()
      };
      const { total, discountEach, taxEach } = getTotalAndDeductionsByPrice(price, taxRate, discount);

      if (typeof limit.discountMax === 'number') expect(discountEach).toBeLessThanOrEqual(limit.discountMax);
      if (typeof limit.discountMin === 'number') expect(discountEach).toBeGreaterThanOrEqual(limit.discountMin);
      expect(new Decimal(price).sub(discountEach).add(taxEach).toNumber()).toBe(total);
    })));
  });
});

describe('getDiscountAmountExTax', () => {
  it('keeps applying min/max to Dollar and Percent discounts', () => {
    expect(getDiscountAmountExTax({ discountType: 'Dollar', discountValue: 50, rounding: 'No Rounding', discountMax: 20 }, 0.1, 100).toNumber()).toBe(20);
    expect(getDiscountAmountExTax({ discountType: 'Dollar', discountValue: 5, rounding: 'No Rounding', discountMin: 10 }, 0.1, 100).toNumber()).toBe(10);
    expect(getDiscountAmountExTax({ discountType: 'Percent', discountPercent: 0.5, rounding: 'No Rounding', discountMax: 30 }, 0.1, 100).toNumber()).toBe(30);
    expect(getDiscountAmountExTax({ discountType: 'Percent', discountPercent: 0.2, rounding: 'No Rounding', discountMin: 10, discountMax: 30 }, 0.1, 100).toNumber()).toBe(20);
  });
});
