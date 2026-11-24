/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { CourseClassDiscount, Discount } from '@api/model';
import { Decimal } from 'decimal.js-light';
import {
  getFeeExTaxByFeeIncTax,
  getPriceAndDeductionsByTotal,
  getTotalAndDeductionsByPrice,
  getTotalByFeeExTax
} from '../../../js/common/utils/financial';
import { getClassDiscountedFee } from '../../../js/containers/entities/courseClasses/components/budget/utils';
import { getInvoiceLineDiscountRecalculation, setInvoiceLinesTotal } from '../../../js/containers/entities/invoices/utils';
import { InvoiceLineWithTotal, InvoiceWithTotalLine } from '../../../js/model/entities/Invoice';

// Rounding cases reported by QA in Jira, with the numbers from the tickets.
// Test names start with the ticket key, so the original report can be found.

const gst = 0.1;

const percent = (discountPercent: number, rounding: Discount['rounding'] = 'No Rounding'): Discount =>
  ({ name: 'Percent', discountType: 'Percent', discountPercent, rounding });

const dollar = (discountValue: number, rounding: Discount['rounding'] = 'No Rounding'): Discount =>
  ({ name: 'Dollar', discountType: 'Dollar', discountValue, rounding });

const classDiscount = (discount: Discount, discountOverride: number = null): CourseClassDiscount => ({ discount, discountOverride });

const lineTotal = (total: number, quantity: number) => new Decimal(total).mul(quantity).toNumber();

describe('fee entered inc GST', () => {
  it.each([38, 50, 72, 93, 380, 545, 1345])('CS-107, CS-160: $%s inc GST is kept without an extra cent', feeIncTax => {
    const feeExTax = getFeeExTaxByFeeIncTax(gst, feeIncTax);

    expect(getTotalByFeeExTax(gst, feeExTax)).toBe(feeIncTax);
    expect(getTotalAndDeductionsByPrice(feeExTax, gst, null).total).toBe(feeIncTax);
  });
});

describe('manual invoice line', () => {
  it('CS-244 I: discount typed in for $1222.73 keeps the price and gives $995', () => {
    expect(getTotalAndDeductionsByPrice(1222.73, gst, 318.18)).toEqual({ discountEach: 318.18, taxEach: 90.45, total: 995 });
  });

  it('CS-244 II: $78 Dollar discount is $78 off price ex tax, not -$85.89', () => {
    expect(getTotalAndDeductionsByPrice(1000, gst, dollar(78))).toEqual({ discountEach: 78, taxEach: 92.2, total: 1014.2 });
    expect(getPriceAndDeductionsByTotal(1014.2, gst, dollar(78))).toEqual({ priceEachExTax: 1000, discountAmount: 78, taxEach: 92.2 });
  });

  it('CS-244 III: $1222.73 x 3 with $318.18 Dollar discount is $2985, also when the line is opened again, not $3440.46', async () => {
    const line = getTotalAndDeductionsByPrice(1222.73, gst, dollar(318.18));

    expect(line).toEqual({ discountEach: 318.18, taxEach: 90.45, total: 995 });
    expect(lineTotal(line.total, 3)).toBe(2985);

    // saved line is opened with its saved amounts (setInvoiceLinesTotal)
    const saved = await setInvoiceLinesTotal({
      invoiceLines: [{ priceEachExTax: 1222.73, discountEachExTax: line.discountEach, taxEach: line.taxEach, quantity: 3 }]
    } as InvoiceWithTotalLine);

    expect((saved.invoiceLines[0] as InvoiceLineWithTotal).total).toBe(2985);
  });

  it('CS-107, CS-634: opened line keeps the saved total, also with the rounding cent kept in tax', async () => {
    const saved = await setInvoiceLinesTotal({
      invoiceLines: [
        // $38 fee: stored tax 3.455 is sent as 3.45, so that it adds up to the saved $38.00 (InvoiceUtil.getTaxEachToTotal)
        { priceEachExTax: 34.55, discountEachExTax: 0, taxEach: 3.45, quantity: 1 },
        // 15% discount rounded to nearest dollar at 15% tax, saved as $11.00 with the cent in tax
        { priceEachExTax: 11.11, discountEachExTax: 1.54, taxEach: 1.43, quantity: 1 },
        // $380 fee with $34.55 discount, saved as $342.00
        { priceEachExTax: 345.45, discountEachExTax: 34.55, taxEach: 31.1, quantity: 2 }
      ]
    } as InvoiceWithTotalLine);

    expect(saved.invoiceLines.map(l => (l as InvoiceLineWithTotal).total)).toEqual([38, 11, 684]);
  });

  it.each<[string, Discount]>([
    ['$318.18 Dollar', dollar(318.18)],
    ['$318 Dollar', dollar(318)],
    ['50% Percent', percent(0.5)]
  ])('CS-244 IV: removing %s discount from $1000 line gives $1000 price, $100 tax, $1100 total', (_, discount) => {
    const lastEditedFields: ('priceEachExTax' | 'total')[] = [undefined, 'priceEachExTax'];

    lastEditedFields.forEach(lastEditedField => {
      expect(getInvoiceLineDiscountRecalculation(discount, null, 1000, lastEditedField)).toBe('byPrice');
    });
    expect(getTotalAndDeductionsByPrice(1000, gst, null)).toEqual({ discountEach: 0, taxEach: 100, total: 1100 });
  });

  it('CS-244 5: $1000 x 3 with $500 discount is $1650 when the line is opened again, not $3300', () => {
    expect(lineTotal(getTotalAndDeductionsByPrice(1000, gst, 500).total, 3)).toBe(1650);
  });

  it('CS-244 6: removing 50% discount after total $1000 was typed in recalculates from the total', () => {
    expect(getPriceAndDeductionsByTotal(1000, gst, percent(0.5))).toEqual({ priceEachExTax: 1818.18, discountAmount: 909.09, taxEach: 90.91 });

    expect(getInvoiceLineDiscountRecalculation(percent(0.5), null, 1818.18, 'total')).toBe('byLastEditedField');
    expect(getPriceAndDeductionsByTotal(1000, gst, null)).toEqual({ priceEachExTax: 909.09, discountAmount: 0, taxEach: 90.91 });
  });
});

describe('class fee after discount, the same on invoice line and in class (edit view panel and budget discount)', () => {
  it('CS-244 7: $1222.73 with 50% discount is $672.50, not $672.49 in class', () => {
    expect(getTotalAndDeductionsByPrice(1222.73, gst, percent(0.5))).toEqual({ discountEach: 611.37, taxEach: 61.14, total: 672.5 });
    expect(getClassDiscountedFee(classDiscount(percent(0.5)), gst, 1222.73)).toBe(672.5);
  });

  it('CS-493: $10 Dollar discount is applied ex tax, $295 class fee becomes $284', () => {
    expect(getTotalAndDeductionsByPrice(268.18, gst, dollar(10))).toEqual({ discountEach: 10, taxEach: 25.82, total: 284 });
    expect(getClassDiscountedFee(classDiscount(dollar(10)), gst, 268.18)).toBe(284);
  });

  it('CS-424: $9.09 Dollar discount takes $10 inc GST off $295 class fee', () => {
    expect(getTotalAndDeductionsByPrice(268.18, gst, dollar(9.09)).total).toBe(285);
    expect(getClassDiscountedFee(classDiscount(dollar(9.09)), gst, 268.18)).toBe(285);
  });

  it.each([20, 50, 100, 295, 380, 1345])('CS-160: $9 Dollar discount rounded to nearest dollar takes $10 off $%s inc GST', feeIncTax => {
    const feeExTax = getFeeExTaxByFeeIncTax(gst, feeIncTax);
    const discount = dollar(9, 'Nearest dollar');

    expect(getTotalAndDeductionsByPrice(feeExTax, gst, discount).total).toBe(feeIncTax - 10);
    expect(getClassDiscountedFee(classDiscount(discount), gst, feeExTax)).toBe(feeIncTax - 10);
  });

  it('CS-455: $449 inc GST with 9.5% discount rounded to nearest 10 cents is $406.30', () => {
    const feeExTax = getFeeExTaxByFeeIncTax(gst, 449);
    const discount = percent(0.095, 'Nearest 10 cents');

    expect(feeExTax).toBe(408.18);
    expect(getTotalAndDeductionsByPrice(feeExTax, gst, discount)).toEqual({ discountEach: 38.82, taxEach: 36.94, total: 406.3 });
    expect(getClassDiscountedFee(classDiscount(discount), gst, feeExTax)).toBe(406.3);
  });

  it('CS-546: $100 class fee without tax with 10% discount is $90 and with 50% discount is $50', () => {
    expect(getClassDiscountedFee(classDiscount(percent(0.1)), 0, 100)).toBe(90);
    expect(getClassDiscountedFee(classDiscount(percent(0.5)), 0, 100)).toBe(50);
    expect(getTotalAndDeductionsByPrice(100, 0, percent(0.1))).toEqual({ discountEach: 10, taxEach: 0, total: 90 });
  });

  it('CS-107: $599 inc GST with 3% discount is $581.03 with $52.82 tax', () => {
    const feeExTax = getFeeExTaxByFeeIncTax(gst, 599);

    expect(feeExTax).toBe(544.55);
    expect(getTotalAndDeductionsByPrice(feeExTax, gst, percent(0.03))).toEqual({ discountEach: 16.34, taxEach: 52.82, total: 581.03 });
    expect(getClassDiscountedFee(classDiscount(percent(0.03)), gst, feeExTax)).toBe(581.03);
  });

  it('CS-107, SST-136: $11 inc GST with 3.5% discount is $10.62 on invoice line and in class', () => {
    const feeExTax = getFeeExTaxByFeeIncTax(gst, 11);

    expect(getTotalAndDeductionsByPrice(feeExTax, gst, percent(0.035))).toEqual({ discountEach: 0.35, taxEach: 0.97, total: 10.62 });
    expect(getClassDiscountedFee(classDiscount(percent(0.035)), gst, feeExTax)).toBe(10.62);
  });

  it('OD-18286: 100% discount makes $269 class fee free', () => {
    const feeExTax = getFeeExTaxByFeeIncTax(gst, 269);

    expect(getTotalAndDeductionsByPrice(feeExTax, gst, percent(1))).toEqual({ discountEach: 244.55, taxEach: 0, total: 0 });
    expect(getClassDiscountedFee(classDiscount(percent(1)), gst, feeExTax)).toBe(0);
  });

  it('uses class discount override instead of discount amount and does not limit it by discount min/max, like server', () => {
    const limited = { ...percent(0.5), discountMax: 20 };

    expect(getClassDiscountedFee(classDiscount(limited), gst, 1222.73)).toBe(1323);
    expect(getClassDiscountedFee(classDiscount(limited, 100), gst, 1222.73)).toBe(1235);
    expect(getClassDiscountedFee(classDiscount(percent(0.5), 2000), gst, 1222.73)).toBe(0);
  });
});
