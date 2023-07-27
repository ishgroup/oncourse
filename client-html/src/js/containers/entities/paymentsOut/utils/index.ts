/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { PaymentOutModel } from "../reducers/state";
import { PaymentOut } from "@api/model";
import { format } from "date-fns";
import { YYYY_MM_DD_MINUSED } from  "ish-ui";

export const getTotalOwing = invoices => invoices.reduce((acc, invoice) => Math.round(acc * 100 + invoice.amountOwing * 100) / 100, 0);

export const getTotalOutstanding = invoices => invoices.reduce((acc, invoice) => Math.round(acc * 100 + invoice.outstanding * 100) / 100, 0);

export const getInitialTotalOwing = invoices => {
  if (Array.isArray(invoices)) {
    return invoices
      .filter(invoice => invoice.amountOwing < 0)
      .reduce((acc, invoice) => Math.round(acc * 100 + invoice.amountOwing * 100) / 100, 0);
  }
  return null;
};

export const getInitialTotalOutstanding = (invoices, amount) => {
  const totalOwing = getInitialTotalOwing(invoices);

  return Math.round((totalOwing || 1) * 100 + amount * 100) / 100;
};

export const getAmountToAllocate = (invoices, amount) => {
  const checkedInvoices = invoices.filter(invoice => invoice.payable);
  const checkedSum = getTotalOwing(checkedInvoices);

  return Math.round(amount * 100 + checkedSum * 100) / 100;
};

export const getPaymentOutFromModel = (model: PaymentOutModel): PaymentOut => {
  const {
    amount,
    chequeSummary,
    datePayed: unformattedDatePayed,
    invoices,
    payeeId,
    refundableId,
    paymentMethodId,
    privateNotes,
    administrationCenterId,
    selectedPaymentMethod
  } = model;
  const datePayed = format(new Date(unformattedDatePayed), YYYY_MM_DD_MINUSED);
  const paymentOut: PaymentOut = {
    amount,
    datePayed,
    payeeId,
    paymentMethodId,
    privateNotes,
    administrationCenterId
  };

  if (selectedPaymentMethod === "Cheque") {
    paymentOut.chequeSummary = chequeSummary;
  }

  if (selectedPaymentMethod === "Credit card") {
    paymentOut.refundableId = refundableId;
  }

  paymentOut.invoices = invoices
    .map(i => ({
      id: i.id,
      amount: Math.round(i.outstanding * 100 - i.amountOwing * 100) / 100
    }))
    .filter(i => i.amount > 0);

  return paymentOut;
};