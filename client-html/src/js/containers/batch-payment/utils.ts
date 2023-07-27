/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { CheckoutModel } from "@api/model";
import { format } from "date-fns-tz";
import { YYYY_MM_DD_MINUSED } from  "ish-ui";
import { BatchPaymentContact } from "../../model/batch-payment";

export const getBachCheckoutModel = (contact: BatchPaymentContact):CheckoutModel =>
  ({
    payerId: contact.id,
    paymentMethodId: null,
    payNow: contact.total,
    paymentDate: format(new Date(), YYYY_MM_DD_MINUSED),
    merchantReference: null,
    contactNodes: [{
      contactId: contact.id,
      enrolments: [],
      memberships: [],
      vouchers: [],
      products: [],
      sendConfirmation: false
    }],
    sendInvoice: true,
    previousInvoices: contact.items.reduce((p, c) => {
      if (c.checked) {
        p[c.id] = c.amountOwing;
      }
      return p;
    }, {}),
    redeemedVouchers: {},
    allowAutoPay: false,
    payWithSavedCard: true,
    payForThisInvoice: 0,
    invoiceDueDate: null
  });
