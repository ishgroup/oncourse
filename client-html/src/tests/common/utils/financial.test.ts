/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Discount } from '@api/model';
import { Decimal } from 'decimal.js-light';
import {
  getInvoiceLineTotalAndTax,
  getPriceAndDeductionsByTotal,
  getTotalAndDeductionsByPrice
} from '../../../js/common/utils/financial';
import { getDiscountAmountExTax, getDiscountedFee, getRoundingByType } from '../../../js/containers/entities/discounts/utils';

const feeOverride: Discount = {
  name: 'Fee override',
  discountType: 'Fee override',
  rounding: 'Nearest dollar',
  discountValue: 454.54
};

// server ignores tax sent by client for manual invoice line (InvoiceApiService.updateInvoiceLines):
// tax is (price - discount) * rate and total is price - discount + tax rounded half even
const expectSavedByServer = (price: number, discountEach: number, taxEach: number, total: number, taxRate: number) => {
  const afterDiscount = new Decimal(price).sub(discountEach);

  expect(total).toBe(afterDiscount.add(afterDiscount.mul(taxRate)).toDecimalPlaces(2, Decimal.ROUND_HALF_EVEN).toNumber());
  expect(afterDiscount.add(taxEach).toNumber()).toBe(total);
};

// at these tax rates any rounded total inc tax is kept with price and discount in cents, at e.g. 15% it is not
const roundTotalKeepingTaxRates = [0, 0.05, 0.1, 0.125, 0.2, 0.25];

describe('getInvoiceLineTotalAndTax', () => {
  it('rounds total half even, so that $38 total is not saved as $38.01', () => {
    // 34.55 * 1.1 = 38.005
    expect(getInvoiceLineTotalAndTax(34.55, 0, 0.1)).toEqual({ total: 38, taxEach: 3.45 });
    // 45.45 * 1.1 = 49.995
    expect(getInvoiceLineTotalAndTax(45.45, 0, 0.1)).toEqual({ total: 50, taxEach: 4.55 });
  });

  it('gives a cent more than rounded total when it can not be kept at 15%', () => {
    // 9.56 * 1.15 = 10.994 and 9.57 * 1.15 = 11.0055, so no price in cents gives $11.00
    expect(getInvoiceLineTotalAndTax(9.56, 0, 0.15)).toEqual({ total: 10.99, taxEach: 1.43 });
    expect(getInvoiceLineTotalAndTax(9.57, 0, 0.15)).toEqual({ total: 11.01, taxEach: 1.44 });
  });

  it('calculates tax from price minus discount', () => {
    expect(getInvoiceLineTotalAndTax(1000, 545.45, 0.1)).toEqual({ total: 500, taxEach: 45.45 });
    expect(getInvoiceLineTotalAndTax(-100, -10, 0.1)).toEqual({ total: -99, taxEach: -9 });
  });
});

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

  it('gives total the server saves when rounded Fee override can not be kept at 15%', () => {
    const discount: Discount = { discountType: 'Fee override', rounding: 'Nearest dollar', discountValue: 9.57 };
    const { total, discountEach, taxEach } = getTotalAndDeductionsByPrice(2000, 0.15, discount);

    // rounded Fee override is $11.00, but price minus discount 9.57 is saved as 11.0055 -> $11.01
    expect(getDiscountedFee(discount, 0.15, 2000).toNumber()).toBe(11);
    expect(total).toBe(11.01);
    expect(discountEach).toBe(1990.43);
    expect(taxEach).toBe(1.44);
  });

  it.each([...roundTotalKeepingTaxRates, 0.15])(
    'gives total the server saves and keeps rounded Fee override where tax rate allows, tax rate %s',
    taxRate => {
      const taxMul = new Decimal(1).add(taxRate);

      for (let fee = 1; fee < 1000; fee += 3) {
        const discount: Discount = {
          discountType: 'Fee override',
          rounding: 'Nearest dollar',
          discountValue: new Decimal(fee).div(taxMul).toDecimalPlaces(2).toNumber()
        };
        const roundedFee = getRoundingByType(discount.rounding, new Decimal(discount.discountValue).mul(taxMul));

        [1, 99.99, 2000].forEach(price => {
          const { total, discountEach, taxEach } = getTotalAndDeductionsByPrice(price, taxRate, discount);

          expectSavedByServer(price, discountEach, taxEach, total, taxRate);

          if (roundTotalKeepingTaxRates.includes(taxRate)) {
            expect(total).toBe(roundedFee);
          } else {
            expect(Math.abs(new Decimal(total).sub(roundedFee).toNumber())).toBeLessThanOrEqual(0.01);
          }
        });
      }
    }
  );

  it('does not apply Fee override to line with zero price, the same as server', () => {
    expect(getTotalAndDeductionsByPrice(0, 0.1, feeOverride)).toEqual({ total: 0, discountEach: 0, taxEach: 0 });
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
        // known totals from rounding issues (CS-107, CS-244) and a sample of other totals
        const totals: number[] = [0.01, 0.05, 0.5, 11, 38, 50, 72, 100.15, 995, 2985];
        for (let cents = 1; cents <= 10000; cents += 7) totals.push(cents / 100);
        for (let dollars = 101; dollars < 5000; dollars += 13) totals.push(dollars);

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

  it.each([0, 0.1, 0.15])('keeps price minus discount plus tax equal to total for tax rate %s', taxRate => {
    const limits: Pick<Discount, 'discountMin' | 'discountMax'>[] = [{ discountMax: 100 }, { discountMin: 600 }, { discountMin: 50, discountMax: 300 }];

    limits.forEach(limit => [1, 11, 99, 500, 999].forEach(fee => [100, 1000, 2000].forEach(price => {
      (['No Rounding', 'Nearest dollar'] as Discount['rounding'][]).forEach(rounding => {
        const discount: Discount = {
          ...feeOverride,
          ...limit,
          rounding,
          discountValue: new Decimal(fee).div(1 + taxRate).toDecimalPlaces(2).toNumber()
        };
        const { total, discountEach, taxEach } = getTotalAndDeductionsByPrice(price, taxRate, discount);

        // like on server, min/max limit discount before rounding, so only discount without rounding stays strictly within them
        if (rounding === 'No Rounding') {
          if (typeof limit.discountMax === 'number') expect(discountEach).toBeLessThanOrEqual(limit.discountMax);
          // discount is never greater than price, even if discountMin is
          if (typeof limit.discountMin === 'number') expect(discountEach).toBeGreaterThanOrEqual(Math.min(limit.discountMin, price));
        }
        expect(discountEach).toBeLessThanOrEqual(price);
        expect(new Decimal(price).sub(discountEach).add(taxEach).toNumber()).toBe(total);
      });
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

// expected values are taken from server DiscountUtils (types/src/test/groovy/ish/util/DiscountUtilsTest.groovy)
describe('discount rounding matches server', () => {
  const percent = (discountPercent: number, rounding: Discount['rounding']): Discount => ({ discountType: 'Percent', discountPercent, rounding });
  const dollar = (discountValue: number, rounding: Discount['rounding']): Discount => ({ discountType: 'Dollar', discountValue, rounding });

  it.each<[string, Discount, number, number, number]>([
    ['15% to nearest dollar', percent(0.15, 'Nearest dollar'), 120, 18.18, 112],
    ['12.5% to nearest 50 cents', percent(0.125, 'Nearest 50 cents'), 125, 15.45, 120.5],
    ['12.5% to nearest 10 cents', percent(0.125, 'Nearest 10 cents'), 125, 15.64, 120.3],
    ['$20.50 to nearest 50 cents', dollar(20.5, 'Nearest 50 cents'), 144.55, 20.46, 136.5],
    ['$20.50 to nearest dollar', dollar(20.5, 'Nearest dollar'), 144.55, 20.91, 136]
  ])('%s', (_, discount, price, expectedDiscount, expectedTotal) => {
    expect(getDiscountedFee(discount, 0.1, price).toNumber()).toBe(expectedTotal);
    expect(new Decimal(getDiscountAmountExTax(discount, 0.1, price)).toDecimalPlaces(2).toNumber()).toBe(expectedDiscount);

    const { total, discountEach, taxEach } = getTotalAndDeductionsByPrice(price, 0.1, discount);

    expect(total).toBe(expectedTotal);
    expect(discountEach).toBe(expectedDiscount);
    expect(new Decimal(price).sub(discountEach).add(taxEach).toNumber()).toBe(total);
  });

  it('rounds total inc tax, not discount ex tax, for Percent discount', () => {
    expect(getTotalAndDeductionsByPrice(100, 0.1, percent(0.15, 'Nearest dollar'))).toEqual({ total: 94, discountEach: 14.55, taxEach: 8.55 });
  });

  it('rounds total inc tax, not discount ex tax, for Dollar discount', () => {
    expect(getTotalAndDeductionsByPrice(100, 0.1, dollar(7.3, 'Nearest dollar'))).toEqual({ total: 102, discountEach: 7.27, taxEach: 9.27 });
  });

  it('does not discount more than price', () => {
    expect(getTotalAndDeductionsByPrice(5, 0.1, dollar(10, 'Nearest dollar'))).toEqual({ total: 0, discountEach: 5, taxEach: 0 });
  });

  it('ignores zero discount min/max, the same as server', () => {
    expect(getDiscountAmountExTax({ ...percent(0.15, 'No Rounding'), discountMin: 0, discountMax: 0 }, 0.1, 100).toNumber()).toBe(15);
  });

  const roundings: Discount['rounding'][] = ['No Rounding', 'Nearest 10 cents', 'Nearest 50 cents', 'Nearest dollar'];

  describe.each(roundings)('with %s', rounding => {
    it.each([0, 0.1, 0.15, 0.2])('gives total the server saves and rounds it like on server where tax rate allows, tax rate %s', taxRate => {
      const discounts = [percent(0.15, rounding), percent(-0.1, rounding), dollar(7.3, rounding), { ...feeOverride, rounding, discountValue: 45.45 }];

      // sample of prices plus prices of the 15% example (11.11) and of the $38 total (34.55)
      const prices = [11.11, 34.55, 100, 120, 125, 144.55, 995.45];
      for (let cents = 1; cents < 30000; cents += 37) prices.push(cents / 100);

      prices.forEach(price => {
        discounts.forEach(discount => {
          const { total, discountEach, taxEach } = getTotalAndDeductionsByPrice(price, taxRate, discount);

          expectSavedByServer(price, discountEach, taxEach, total, taxRate);

          if (rounding !== 'No Rounding') {
            const roundedFee = getDiscountedFee(discount, taxRate, price).toNumber();

            if (roundTotalKeepingTaxRates.includes(taxRate)) {
              expect(total).toBe(roundedFee);
              expect(getRoundingByType(rounding, new Decimal(total))).toBe(total);
            } else {
              expect(Math.abs(new Decimal(total).sub(roundedFee).toNumber())).toBeLessThanOrEqual(0.01);
            }
          }
        });
      });
    });
  });

  it('gives total the server saves for 15% Percent discount rounded to nearest dollar', () => {
    // client used to show 11.00, the server saved (11.11 - 1.54) * 1.15 = 11.0055 -> 11.01
    expect(getTotalAndDeductionsByPrice(11.11, 0.15, percent(0.15, 'Nearest dollar'))).toEqual({ total: 11.01, discountEach: 1.54, taxEach: 1.44 });
  });
});

describe('credit line (negative price)', () => {
  const percent = (discountPercent: number, rounding: Discount['rounding']): Discount => ({ discountType: 'Percent', discountPercent, rounding });

  it('keeps Percent discount instead of discounting the whole line', () => {
    expect(getTotalAndDeductionsByPrice(-100, 0.1, percent(0.1, 'No Rounding'))).toEqual({ total: -99, discountEach: -10, taxEach: -9 });
  });

  it('keeps Dollar discount instead of discounting the whole line', () => {
    expect(getTotalAndDeductionsByPrice(-100, 0.1, { discountType: 'Dollar', discountValue: 10, rounding: 'No Rounding' }))
      .toEqual({ total: -121, discountEach: 10, taxEach: -11 });
  });

  it.each<Discount['rounding']>(['No Rounding', 'Nearest 10 cents', 'Nearest 50 cents', 'Nearest dollar'])(
    'gives the same amounts as for positive price with opposite sign for Percent discount with %s',
    rounding => {
      // toBe tells -0 from 0, tax is 0 at 0% tax rate
      const negated = (value: number) => (value === 0 ? 0 : -value);

      [0, 0.1, 0.15].forEach(taxRate => [11.11, 100, 144.55, 995.45].forEach(price => {
        const credit = getTotalAndDeductionsByPrice(-price, taxRate, percent(0.15, rounding));
        const debit = getTotalAndDeductionsByPrice(price, taxRate, percent(0.15, rounding));

        expect(credit.total).toBe(negated(debit.total));
        expect(credit.discountEach).toBe(negated(debit.discountEach));
        expect(credit.taxEach).toBe(negated(debit.taxEach));
      }));
    }
  );
});

describe('getRoundingByType', () => {
  it.each<[Discount['rounding'], number, number]>([
    ['Nearest 50 cents', 0.3, 0.5],
    ['Nearest 50 cents', 0.2, 0],
    ['Nearest 50 cents', 12.8, 13],
    ['Nearest 50 cents', 12.24, 12],
    ['Nearest 50 cents', 12.5, 12.5],
    ['Nearest 10 cents', 12.25, 12.3],
    ['Nearest dollar', 12.5, 13],
    ['Nearest dollar', 12.4999, 12]
  ])('%s rounds %s to %s', (rounding, value, expected) => {
    expect(getRoundingByType(rounding, new Decimal(value))).toBe(expected);
  });
});
