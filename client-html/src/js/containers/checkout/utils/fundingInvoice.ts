/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Invoice } from "@api/model";
import { CheckoutSummaryListItem } from "../../../model/checkout";
import { CheckoutFundingInvoice, CheckoutFundingInvoiceItem } from "../../../model/checkout/fundingInvoice";

export const getFundingInvoice = (fundingInvoice: CheckoutFundingInvoiceItem): Invoice => {
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

export const calculateFundingInvoice = (summaryList: CheckoutSummaryListItem[]): CheckoutFundingInvoice => {
  let item: CheckoutFundingInvoiceItem = null;
  const enrolledStudents = summaryList.filter(l =>
    l.contact.isCompany === "false"
    && l.items.some(i => i.checked && i.type === "course" && i.class.relatedFundingSourceId !== null));

  if (enrolledStudents.length === 1) {
    item = {
      enrolment: {
        ...enrolledStudents[0],
        items: [enrolledStudents[0].items.find(i => i.type === "course" && i.checked)]
      }
    };
  }

  return {
    companies: [],
    item,
    trackAmountOwing: Boolean(item)
  };
};
