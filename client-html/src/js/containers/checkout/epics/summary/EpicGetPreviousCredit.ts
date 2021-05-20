/*
 * Copyright ish group pty ltd 2021.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import { Invoice } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import InvoiceService from "../../../entities/invoices/services/InvoiceService";
import {
  CHECKOUT_GET_PREVIOUS_CREDIT,
  CHECKOUT_SET_PREVIOUS_CREDIT
} from "../../actions/checkoutSummary";
import { mergeInvoicePaymentPlans } from "../../utils";

const request: EpicUtils.Request<any, string> = {
  type: CHECKOUT_GET_PREVIOUS_CREDIT,
  getData: id => InvoiceService.searchInvoices(
    `contact.id is ${id} and amountOwing < 0`
  ),
  processData: (items: Invoice[]) => {
    items.forEach(i => {
      i.paymentPlans = mergeInvoicePaymentPlans(i.paymentPlans) as any;
    });

    return [
      {
        type: CHECKOUT_SET_PREVIOUS_CREDIT,
        payload: {
          items
        }
      }
    ];
  }
};

export const EpicGetPreviousCredit: Epic<any, any> = EpicUtils.Create(request);
