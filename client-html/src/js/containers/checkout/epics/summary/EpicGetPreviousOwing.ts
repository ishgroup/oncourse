/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { Invoice } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import InvoiceService from "../../../entities/invoices/services/InvoiceService";
import { CHECKOUT_GET_PREVIOUS_OWING, CHECKOUT_SET_PREVIOUS_OWING } from "../../actions/checkoutSummary";
import { mergeInvoicePaymentPlans } from "../../utils";

const request: EpicUtils.Request<any, string> = {
  type: CHECKOUT_GET_PREVIOUS_OWING,
  getData: id => InvoiceService.searchInvoices(
    `contact.id is ${id} and amountOwing > 0`
  ),
  processData: (items: Invoice[]) => {
    items.forEach(i => {
      i.paymentPlans = mergeInvoicePaymentPlans(i.paymentPlans) as any;
    });

    return [
    {
      type: CHECKOUT_SET_PREVIOUS_OWING,
      payload: {
        items
      }
    }
  ];
}
};

export const EpicGetPreviousOwing: Epic<any, any> = EpicUtils.Create(request);
