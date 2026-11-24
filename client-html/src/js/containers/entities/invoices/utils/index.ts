/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Discount, Invoice, InvoicePaymentPlan } from '@api/model';
import { min } from 'date-fns';
import { Decimal } from 'decimal.js-light';
import { decimalMinus, decimalMul, decimalPlus } from 'ish-ui';
import { bankRounding, getPriceAndDeductionsByTotal } from '../../../../common/utils/financial';
import { getRoundingByType } from '../../discounts/utils';
import { InvoiceWithTotalLine } from '../../../../model/entities/Invoice';

export const preformatInvoice = (value: InvoiceWithTotalLine): Invoice => {
  if (value && value.invoiceLines) {
    value.invoiceLines.forEach(l => {
      delete l.total;
    });
  }
  return value;
};

/**
 * Line totals of saved invoice. Server sends tax each that adds up with price and discount to the saved total each
 * (InvoiceUtil.getTaxEachToTotal), so the total is not calculated again from tax rate, which would lose a rounding cent kept in tax
 */
export const setInvoiceLinesTotal = async (value: InvoiceWithTotalLine): Promise<Invoice> => {
  if (value && value.invoiceLines) {
    value.invoiceLines.forEach(line => {
      const totalEach = bankRounding(new Decimal(line.priceEachExTax || 0).sub(line.discountEachExTax || 0).add(line.taxEach || 0));

      line.total = decimalMul(totalEach, line.quantity);
    });
  }
  return value;
};

export const reducePayments = (payments: InvoicePaymentPlan[]) =>
  (payments.length ? payments.reduce((prev, cur) => decimalPlus(prev, cur.amount), 0) : null);

export const sortInvoicePaymentPlans = (a: InvoicePaymentPlan, b: InvoicePaymentPlan) => {
  if (a.type === "Invoice office") {
    return 0;
  }
  if (b.type === "Invoice office") {
    return 1;
  }

  if (a.date > b.date) {
    return 1;
  }

  if (b.date > a.date) {
    return -1;
  }

  if (a.date === b.date) {
    if (a.entityName === "PaymentIn") {
      return 1;
    }
    if (b.entityName === "PaymentIn") {
      return -1;
    }
    return 0;
  }

  return 0;
};

export const getInvoiceClosestPaymentDueDate = (invoice: Invoice) => {
  const successfulPayments = invoice.paymentPlans
    .filter(p => p.entityName === "PaymentIn" && p.successful)
    .map(p => ({ ...p }));

  const openedDues = invoice.paymentPlans.filter(p => p.entityName === "InvoiceDueDate");

  const datesArr = [];

  let coveringPayment = successfulPayments.reduce((p, c) => decimalPlus(p, c.amount), 0);

  for (let i = 0; i < openedDues.length; i++) {
    const dueDate = new Date(openedDues[i].date);

    const isCovered = coveringPayment >= openedDues[i].amount;

    coveringPayment = decimalMinus(coveringPayment, openedDues[i].amount);

    if (!isCovered) {
      datesArr.push(dueDate);
    }
  }
  return datesArr.length ? min(datesArr) : null;
};

export const processInvoicePaymentPlans = (paymentPlans: InvoicePaymentPlan[]) => {
  const updated = [...paymentPlans];

  updated.sort(sortInvoicePaymentPlans);

  const successfulPayments = updated
    .filter(p => p.entityName === "PaymentIn" && p.successful)
    .map(p => ({ ...p }));

  const openedDues = updated.filter(p => p.entityName === "InvoiceDueDate");

  let coveringPayment = successfulPayments.reduce((p, c) => decimalPlus(p, c.amount), 0);

  for (let i = 0; i < openedDues.length; i++) {
    openedDues[i].successful = coveringPayment >= openedDues[i].amount;
    coveringPayment = decimalMinus(coveringPayment, openedDues[i].amount);
  }

  return updated;
};

export const isInvoiceType = (id: string, records: any) => {
  const invoiceFromRecords = records && records.rows.length && records.rows.find(elem => elem.id === id);

  return invoiceFromRecords && invoiceFromRecords.values.includes("Invoice");
};

const isFeeOverride = (discount: Discount | number): boolean =>
  typeof discount !== 'number' && discount?.discountType === 'Fee override';

export type InvoiceLineDiscountRecalculation = 'feeOverrideTotal' | 'byPrice' | 'byLastEditedField';

/**
 * Amounts of invoice line without price when Fee override is applied (see feeOverrideTotal recalculation).
 * Fee override amount is the fee of one unit, so price, discount and tax are calculated per unit
 * and only the line total is multiplied by quantity
 */
export const getFeeOverrideLineAmounts = (
  discount: Discount,
  taxRate: number,
  quantity: number
): {
  priceEachExTax: number,
  discountEachExTax: number,
  taxEach: number,
  total: number
} => {
  // line without price takes the Fee override as its price, so total is the rounded Fee override inc tax, the same as getDiscountedFee gives for lines with price
  const totalEach = bankRounding(getRoundingByType(discount.rounding, new Decimal(discount.discountValue).mul(new Decimal(taxRate || 0).plus(1))));
  const { priceEachExTax, discountAmount, taxEach } = getPriceAndDeductionsByTotal(totalEach, taxRate, discount);

  return {
    priceEachExTax,
    discountEachExTax: discountAmount,
    taxEach,
    total: decimalMul(totalEach, quantity)
  };
};

export const getInvoiceLineDiscountRecalculation = (
  previousDiscount: Discount | number,
  nextDiscount: Discount | number,
  priceEachExTax: number,
  lastEditedField: 'priceEachExTax' | 'total',
  feeOverridePriceEachExTax?: number,
  taxRate?: number
): InvoiceLineDiscountRecalculation => {
  // credit line has negative price
  const hasPrice = Boolean(priceEachExTax);

  if (isFeeOverride(nextDiscount)) {
    const isPriceSetByFeeOverride = priceEachExTax === feeOverridePriceEachExTax
      || (isFeeOverride(previousDiscount)
        && priceEachExTax === getFeeOverrideLineAmounts(previousDiscount as Discount, taxRate, 1).priceEachExTax);

    return hasPrice && !isPriceSetByFeeOverride ? 'byPrice' : 'feeOverrideTotal';
  }

  // total, tax and discount were set by the Fee override, not typed in by user, so they must not be kept
  if (isFeeOverride(previousDiscount)) {
    return 'byPrice';
  }

  // don't depend on price field being focused, line with price keeps it unless user has typed in total
  return hasPrice && lastEditedField !== 'total' ? 'byPrice' : 'byLastEditedField';
};
