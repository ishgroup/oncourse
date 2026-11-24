/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Discount } from '@api/model';
import { Decimal } from 'decimal.js-light';
import { getCustomColumnsMap } from '../../../js/common/utils/common';
import { getPriceAndDeductionsByTotal, getTotalAndDeductionsByPrice } from '../../../js/common/utils/financial';
import { getRoundingByType, plainDiscountToAPIModel } from '../../../js/containers/entities/discounts/utils';
import { INVOICE_LINE_DISCOUNT_COLUMNS } from '../../../js/containers/entities/invoices/constants';
import { getFeeOverrideLineAmounts, getInvoiceLineDiscountRecalculation } from '../../../js/containers/entities/invoices/utils';

const feeOverride: Discount = {
  name: 'Fee override',
  discountType: 'Fee override',
  rounding: 'Nearest dollar',
  discountValue: 454.54
};

const dollar: Discount = {
  name: 'Dollar',
  discountType: 'Dollar',
  rounding: 'No Rounding',
  discountValue: 10
};

const percent: Discount = {
  name: 'Percent',
  discountType: 'Percent',
  rounding: 'No Rounding',
  discountPercent: 0.1
};

describe('getInvoiceLineDiscountRecalculation', () => {
  const lastEditedFields: ('priceEachExTax' | 'total')[] = [undefined, 'priceEachExTax', 'total'];

  describe('when Fee override is selected', () => {
    it.each(lastEditedFields)('uses Fee override amount as total for line without price, last edited field %s', lastEditedField => {
      expect(getInvoiceLineDiscountRecalculation(null, feeOverride, 0, lastEditedField)).toBe('feeOverrideTotal');
      expect(getInvoiceLineDiscountRecalculation(null, feeOverride, undefined, lastEditedField)).toBe('feeOverrideTotal');
      expect(getInvoiceLineDiscountRecalculation(dollar, feeOverride, 0, lastEditedField)).toBe('feeOverrideTotal');
    });

    it.each(lastEditedFields)('recalculates by price for line with price, last edited field %s', lastEditedField => {
      expect(getInvoiceLineDiscountRecalculation(null, feeOverride, 100, lastEditedField)).toBe('byPrice');
      expect(getInvoiceLineDiscountRecalculation(dollar, feeOverride, 100, lastEditedField)).toBe('byPrice');
      expect(getInvoiceLineDiscountRecalculation(feeOverride, feeOverride, 100, lastEditedField)).toBe('byPrice');
    });

    it.each(lastEditedFields)('does not use price derived from previous Fee override as list price, last edited field %s', lastEditedField => {
      [feeOverride, dollar, null].forEach(previous => {
        expect(getInvoiceLineDiscountRecalculation(previous, feeOverride, 454.55, lastEditedField, 454.55)).toBe('feeOverrideTotal');
      });
    });

    it.each(lastEditedFields)('does not use price of saved line derived from its Fee override as list price, last edited field %s', lastEditedField => {
      // line opened again, so price written by Fee override is not known, but it is the price this Fee override gives to line without price
      expect(getInvoiceLineDiscountRecalculation(feeOverride, dollar, 454.55, lastEditedField, undefined, 0.1)).toBe('byPrice');
      expect(getInvoiceLineDiscountRecalculation(feeOverride, feeOverride, 454.55, lastEditedField, undefined, 0.1)).toBe('feeOverrideTotal');
      expect(getInvoiceLineDiscountRecalculation(feeOverride, feeOverride, 1000, lastEditedField, undefined, 0.1)).toBe('byPrice');
      expect(getInvoiceLineDiscountRecalculation(dollar, feeOverride, 454.55, lastEditedField, undefined, 0.1)).toBe('byPrice');
    });

    it.each(lastEditedFields)('recalculates by price when user has changed price derived from Fee override, last edited field %s', lastEditedField => {
      expect(getInvoiceLineDiscountRecalculation(feeOverride, feeOverride, 1000, lastEditedField, 454.55)).toBe('byPrice');
    });
  });

  describe('when switching away from Fee override', () => {
    it.each(lastEditedFields)('recalculates by price when Fee override is replaced or removed, last edited field %s', lastEditedField => {
      [dollar, percent, null, undefined].forEach(next => {
        expect(getInvoiceLineDiscountRecalculation(feeOverride, next, 454.55, lastEditedField)).toBe('byPrice');
        expect(getInvoiceLineDiscountRecalculation(feeOverride, next, 0, lastEditedField)).toBe('byPrice');
      });
    });
  });

  describe('when selected discount is re-applied by locking after discount each ex tax was edited manually', () => {
    it.each([undefined, 'priceEachExTax'] as const)('recalculates by price for line with price, last edited field %s', lastEditedField => {
      [feeOverride, dollar, percent].forEach(selected => {
        expect(getInvoiceLineDiscountRecalculation(12.5, selected, 100, lastEditedField)).toBe('byPrice');
      });
    });

    it('uses Fee override amount as total when line price was derived from it', () => {
      expect(getInvoiceLineDiscountRecalculation(12.5, feeOverride, 454.55, undefined, 454.55)).toBe('feeOverrideTotal');
    });
  });

  describe('when other discount is selected or removed', () => {
    it.each([undefined, 'priceEachExTax'] as const)(
      'recalculates by price for line with price even if price field was not focused, last edited field %s',
      lastEditedField => {
        [dollar, percent, null].forEach(next => {
          expect(getInvoiceLineDiscountRecalculation(null, next, 100, lastEditedField)).toBe('byPrice');
          expect(getInvoiceLineDiscountRecalculation(percent, next, 100, lastEditedField)).toBe('byPrice');
        });
      }
    );

    it('keeps total typed in by user', () => {
      [dollar, percent, null].forEach(next => {
        expect(getInvoiceLineDiscountRecalculation(null, next, 100, 'total')).toBe('byLastEditedField');
        expect(getInvoiceLineDiscountRecalculation(percent, next, 100, 'total')).toBe('byLastEditedField');
      });
    });

    it.each(lastEditedFields)('falls back to last edited field for line without price, last edited field %s', lastEditedField => {
      [dollar, percent, null].forEach(next => {
        expect(getInvoiceLineDiscountRecalculation(null, next, 0, lastEditedField)).toBe('byLastEditedField');
      });
    });
  });

  describe('on credit line (negative price)', () => {
    it.each([undefined, 'priceEachExTax'] as const)('recalculates by price, last edited field %s', lastEditedField => {
      [dollar, percent, feeOverride, null].forEach(next => {
        expect(getInvoiceLineDiscountRecalculation(null, next, -100, lastEditedField)).toBe('byPrice');
        expect(getInvoiceLineDiscountRecalculation(percent, next, -100, lastEditedField)).toBe('byPrice');
      });
    });

    it('keeps negative price and gives credit of Fee override amount when Fee override is selected', () => {
      expect(getInvoiceLineDiscountRecalculation(null, feeOverride, -100, undefined, undefined, 0.1)).toBe('byPrice');
      expect(getTotalAndDeductionsByPrice(-100, 0.1, feeOverride)).toEqual({ total: -500, discountEach: 354.55, taxEach: -45.45 });
    });
  });
});

describe('Invoice line discount loaded as plain record', () => {
  // values in the order of INVOICE_LINE_DISCOUNT_COLUMNS
  const toDiscount = (values: string[]): Discount => plainDiscountToAPIModel(getCustomColumnsMap(INVOICE_LINE_DISCOUNT_COLUMNS)({ id: '1', values }));

  it('limits discount by discountMax, the same as checkout and server', () => {
    const discount = toDiscount(['Half price', 'Percent', null, '0.5', 'No Rounding', null, '20.00']);

    expect(discount.discountMax).toBe(20);
    expect(getTotalAndDeductionsByPrice(100, 0.1, discount)).toEqual({ total: 88, discountEach: 20, taxEach: 8 });
  });

  it('limits discount by discountMin and ignores zero discountMax', () => {
    const discount = toDiscount(['Five dollars', 'Dollar', '5.00', null, 'No Rounding', '10.00', '0.00']);

    expect(discount).toMatchObject({ discountValue: 5, discountMin: 10, discountMax: 0 });
    expect(getTotalAndDeductionsByPrice(100, 0.1, discount)).toEqual({ total: 99, discountEach: 10, taxEach: 9 });
  });
});

describe('Switching discount on invoice line', () => {
  it('recalculates line when Fee override on line without price is replaced with Dollar discount', () => {
    const taxRate = 0.1;

    // line with no price, Fee override selected
    expect(getInvoiceLineDiscountRecalculation(null, feeOverride, 0, undefined)).toBe('feeOverrideTotal');
    const feeOverrideTotal = getRoundingByType(feeOverride.rounding, new Decimal(feeOverride.discountValue).mul(1 + taxRate));
    const afterFeeOverride = getPriceAndDeductionsByTotal(feeOverrideTotal, taxRate, feeOverride);

    expect(feeOverrideTotal).toBe(500);
    expect(afterFeeOverride).toEqual({ priceEachExTax: 454.55, discountAmount: 0, taxEach: 45.45 });

    // Fee override replaced with Dollar discount, neither price nor total field was focused
    expect(getInvoiceLineDiscountRecalculation(feeOverride, dollar, afterFeeOverride.priceEachExTax, undefined)).toBe('byPrice');
    const afterDollar = getTotalAndDeductionsByPrice(afterFeeOverride.priceEachExTax, taxRate, dollar);

    expect(afterDollar).toEqual({ total: 489, discountEach: 10, taxEach: 44.45 });
    expect(new Decimal(afterFeeOverride.priceEachExTax).sub(afterDollar.discountEach).add(afterDollar.taxEach).toNumber()).toBe(afterDollar.total);
  });
});

describe('Picking Fee overrides on invoice line without price', () => {
  type Line = { priceEachExTax: number, discountEachExTax: number, taxEach: number, total: number };

  // mirrors applyDiscount in InvoiceLines.tsx
  const applyFeeOverride = (
    line: Line,
    previousDiscount: Discount,
    discount: Discount,
    taxRate: number,
    feeOverridePriceEachExTax: number
  ): { line: Line, feeOverridePriceEachExTax: number } => {
    const recalculation = getInvoiceLineDiscountRecalculation(previousDiscount, discount, line.priceEachExTax, undefined, feeOverridePriceEachExTax, taxRate);

    if (recalculation === 'feeOverrideTotal') {
      const amounts = getFeeOverrideLineAmounts(discount, taxRate, 1);

      return { line: amounts, feeOverridePriceEachExTax: amounts.priceEachExTax };
    }

    const { total, discountEach, taxEach } = getTotalAndDeductionsByPrice(line.priceEachExTax, taxRate, discount);

    return { line: { ...line, discountEachExTax: discountEach, taxEach, total }, feeOverridePriceEachExTax };
  };

  const emptyLine: Line = { priceEachExTax: 0, discountEachExTax: 0, taxEach: 0, total: 0 };

  const overrideByFee = (fee: number, taxRate: number): Discount => ({
    name: `Fee override ${fee}`,
    discountType: 'Fee override',
    rounding: 'Nearest dollar',
    discountValue: new Decimal(fee).div(1 + taxRate).toDecimalPlaces(2).toNumber()
  });

  it('gives the same price and discount whatever order Fee overrides are picked in', () => {
    const taxRate = 0.1;
    const a = overrideByFee(500, taxRate);
    const b = overrideByFee(300, taxRate);

    const afterA = applyFeeOverride(emptyLine, null, a, taxRate, undefined);
    expect(afterA.line).toEqual({ priceEachExTax: 454.55, discountEachExTax: 0, taxEach: 45.45, total: 500 });

    const afterAB = applyFeeOverride(afterA.line, a, b, taxRate, afterA.feeOverridePriceEachExTax);
    const onlyB = applyFeeOverride(emptyLine, null, b, taxRate, undefined);

    expect(onlyB.line).toEqual({ priceEachExTax: 272.73, discountEachExTax: 0, taxEach: 27.27, total: 300 });
    expect(afterAB.line).toEqual(onlyB.line);
  });

  it.each([0, 0.1, 0.15])('is independent of picking order for any pair of Fee overrides at tax rate %s', taxRate => {
    const fees = [1, 11, 99, 300, 500, 999];

    fees.forEach(feeA => fees.forEach(feeB => {
      const a = overrideByFee(feeA, taxRate);
      const b = overrideByFee(feeB, taxRate);

      const afterA = applyFeeOverride(emptyLine, null, a, taxRate, undefined);
      const afterAB = applyFeeOverride(afterA.line, a, b, taxRate, afterA.feeOverridePriceEachExTax);
      const afterABA = applyFeeOverride(afterAB.line, b, a, taxRate, afterAB.feeOverridePriceEachExTax);

      expect(afterAB.line).toEqual(applyFeeOverride(emptyLine, null, b, taxRate, undefined).line);
      expect(afterABA.line).toEqual(afterA.line);
    }));
  });

  it('gives the same price and discount for saved line opened again whatever order Fee overrides are picked in', () => {
    [0, 0.1, 0.15].forEach(taxRate => {
      const a = overrideByFee(500, taxRate);
      const b = overrideByFee(300, taxRate);
      const savedWithA = applyFeeOverride(emptyLine, null, a, taxRate, undefined).line;

      // feeOverridePriceEachExTax is not known after the line is opened again
      const recalculation = getInvoiceLineDiscountRecalculation(a, b, savedWithA.priceEachExTax, undefined, undefined, taxRate);

      expect(recalculation).toBe('feeOverrideTotal');
      expect(getFeeOverrideLineAmounts(b, taxRate, 1)).toEqual(applyFeeOverride(emptyLine, null, b, taxRate, undefined).line);
    });
  });

  it('keeps price entered by user when Fee overrides are switched', () => {
    const taxRate = 0.1;
    const a = overrideByFee(500, taxRate);
    const b = overrideByFee(300, taxRate);
    const line = { ...emptyLine, priceEachExTax: 1000 };

    const afterA = applyFeeOverride(line, null, a, taxRate, undefined);
    const afterAB = applyFeeOverride(afterA.line, a, b, taxRate, afterA.feeOverridePriceEachExTax);

    expect(afterA.line).toEqual({ priceEachExTax: 1000, discountEachExTax: 545.45, taxEach: 45.45, total: 500 });
    expect(afterAB.line).toEqual({ priceEachExTax: 1000, discountEachExTax: 727.27, taxEach: 27.27, total: 300 });
  });
});

describe('getFeeOverrideLineAmounts', () => {
  const overrideByFee = (fee: number, taxRate: number): Discount => ({
    name: `Fee override ${fee}`,
    discountType: 'Fee override',
    rounding: 'Nearest dollar',
    discountValue: new Decimal(fee).div(1 + taxRate).toDecimalPlaces(2).toNumber()
  });

  it('calculates price, discount and tax per unit and multiplies only total by quantity', () => {
    expect(getFeeOverrideLineAmounts(overrideByFee(500, 0.1), 0.1, 2)).toEqual({
      priceEachExTax: 454.55,
      discountEachExTax: 0,
      taxEach: 45.45,
      total: 1000
    });
  });

  it('keeps rounded Fee override as total at 15% and puts the rounding cent into tax', () => {
    // Fee override $11.00 inc tax gives price 9.57 and 9.57 * 1.15 = 11.0055, tax takes the difference to $11.00
    expect(getFeeOverrideLineAmounts({ ...feeOverride, discountValue: 9.57 }, 0.15, 2)).toEqual({
      priceEachExTax: 9.57,
      discountEachExTax: 0,
      taxEach: 1.43,
      total: 22
    });
  });

  it.each([0, 0.1, 0.15])('keeps line amounts consistent with quantity at tax rate %s', taxRate => {
    [1, 11, 99, 500, 999].forEach(fee => {
      const discount = overrideByFee(fee, taxRate);
      const each = getFeeOverrideLineAmounts(discount, taxRate, 1);

      [1, 2, 3, 7].forEach(quantity => {
        const line = getFeeOverrideLineAmounts(discount, taxRate, quantity);

        // per unit amounts don't depend on quantity
        expect(line.priceEachExTax).toBe(each.priceEachExTax);
        expect(line.discountEachExTax).toBe(each.discountEachExTax);
        expect(line.taxEach).toBe(each.taxEach);
        expect(line.total).toBe(new Decimal(each.total).mul(quantity).toNumber());
        expect(new Decimal(line.priceEachExTax).sub(line.discountEachExTax).add(line.taxEach).mul(quantity).toNumber()).toBe(line.total);

        // quantity blur recalculates by price, it must not change the line
        const afterQuantityBlur = getTotalAndDeductionsByPrice(line.priceEachExTax, taxRate, discount);

        expect(afterQuantityBlur.discountEach).toBe(line.discountEachExTax);
        expect(afterQuantityBlur.taxEach).toBe(line.taxEach);
        expect(new Decimal(afterQuantityBlur.total).mul(quantity).toNumber()).toBe(line.total);
      });
    });
  });
});
