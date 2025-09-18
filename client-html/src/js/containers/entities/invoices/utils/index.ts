/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Invoice, InvoicePaymentPlan } from '@api/model';
import { min } from 'date-fns';
import { decimalMinus, decimalMul, decimalPlus } from 'ish-ui';
import EntityService from '../../../../common/services/EntityService';
import { getCustomColumnsMap } from '../../../../common/utils/common';
import { getTotalAndDeductionsByPrice } from '../../../../common/utils/financial';
import { InvoiceWithTotalLine } from '../../../../model/entities/Invoice';
import { plainDiscountToAPIModel } from '../../discounts/utils';
import { INVOICE_LINE_DISCOUNT_AQL, INVOICE_LINE_DISCOUNT_COLUMNS } from '../constants';

export const preformatInvoice = (value: InvoiceWithTotalLine): Invoice => {
  if (value && value.invoiceLines) {
    value.invoiceLines.forEach(l => {
      delete l.total;
    });
  }
  return value;
};

export const setInvoiceLinesTotal = async (value: InvoiceWithTotalLine): Promise<Invoice> => {
  if (value && value.invoiceLines) {
    for (const line of value.invoiceLines) {
      const discount = line.discountId && await EntityService.getPlainRecords(
        'Discount',
        INVOICE_LINE_DISCOUNT_COLUMNS,
        `id is ${line.discountId} and ${INVOICE_LINE_DISCOUNT_AQL}`,
        1,
        0,
      ).then(({ rows }) => plainDiscountToAPIModel(rows.map(getCustomColumnsMap(INVOICE_LINE_DISCOUNT_COLUMNS))[0]));

      const taxRate = await EntityService.getPlainRecords(
        'Tax',
        'rate',
        `id is ${line.taxId}`,
        1,
        0,
      ).then(({ rows }) => parseFloat(rows.map(r => r.values[0])[0]));
      
      const { total } = getTotalAndDeductionsByPrice(line.priceEachExTax, taxRate, discount);
      
      line.total = decimalMul(total, line.quantity);
    }
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