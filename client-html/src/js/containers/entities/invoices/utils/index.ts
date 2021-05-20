/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { min } from "date-fns";
import { Invoice, InvoicePaymentPlan } from "@api/model";
import { InvoiceWithTotalLine } from "../../../../model/entities/Invoice";
import { decimalMinus, decimalPlus } from "../../../../common/utils/numbers/decimalCalculation";

export const preformatInvoice = (value: InvoiceWithTotalLine): Invoice => {
  if (value && value.invoiceLines) {
    value.invoiceLines.forEach(l => {
      delete l.total;
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
    openedDues[i].successful = false;

    let isCovered = false;

    if (coveringPayment >= openedDues[i].amount) {
      isCovered = true;
      openedDues[i].successful = true;
    }

    coveringPayment = decimalMinus(coveringPayment, openedDues[i].amount);

    if (!isCovered) {
      datesArr.push(dueDate);
    }
  }
  return datesArr.length ? min(datesArr) : null;
};
