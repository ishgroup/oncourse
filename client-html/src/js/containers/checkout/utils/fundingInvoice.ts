/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Invoice } from "@api/model";
import { CheckoutFundingInvoice } from "../../../model/checkout/fundingInvoice";

export const getFundingInvoices = (fundingInvoices: CheckoutFundingInvoice[]): Invoice[] => {
  const invoices: Invoice[] = [];
  fundingInvoices.forEach(fi => {
    const invoice: Invoice = {};
    invoice.contactId = fi.fundingProviderId;
    invoice.paymentPlans = fi.paymentPlans.filter(pp => pp.amount).map(pp => ({
      ...pp,
      id: null
    }));
    invoice.total = fi.total;
    invoice.customerReference = fi.vetPurchasingContractID;
    invoice.relatedFundingSourceId = fi.relatedFundingSourceId;
    invoice.invoiceLines = [];
    fi.item.enrolment.items.forEach(ei => {
      invoice.invoiceLines.push({
        finalPriceToPayIncTax: ei.totalFee,
        courseClassId: ei.class.id
      });
    });
    invoices.push(invoice);
  });
  return invoices;
};
