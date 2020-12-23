/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Invoice } from "@api/model";
import { CheckoutFundingInvoice } from "../../../model/checkout/fundingInvoice";

export const getFundingInvoice = (fundingInvoice: CheckoutFundingInvoice): Invoice => {
  let invoice: Invoice = null;
  if (fundingInvoice) {
    invoice = {};
    invoice.contactId = fundingInvoice.fundingProviderId;
    invoice.paymentPlans = fundingInvoice.paymentPlans.filter(pp => pp.amount);
    invoice.total = fundingInvoice.total;
    invoice.customerReference = fundingInvoice.vetPurchasingContractID;
  }
  return invoice;
};
