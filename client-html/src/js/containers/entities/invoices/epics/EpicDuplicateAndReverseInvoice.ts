/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import InvoiceService from "../services/InvoiceService";
import { DUPLICATE_AND_REVERSE_INVOICE_ITEM } from "../actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { initialize } from "redux-form";
import { Invoice } from "@api/model";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import { formatToDateOnly } from "../../../../common/utils/dates/datesNormalizing";

const request: EpicUtils.Request = {
  type: DUPLICATE_AND_REVERSE_INVOICE_ITEM,
  getData: id => {
    return InvoiceService.getInvoice(id);
  },
  processData: (data: Invoice) => {
    data.invoiceLines.forEach(l => {
      l.priceEachExTax = -l.priceEachExTax;
      l.discountEachExTax = -l.discountEachExTax;
      l.taxEach = -l.taxEach;
    });

    data.paymentPlans = [data.paymentPlans[0]];

    data.paymentPlans[0].amount = -data.paymentPlans[0].amount;

    data.total = -data.total;
    data.amountOwing = data.total;
    data.invoiceDate = formatToDateOnly(new Date());
    data.dateDue = formatToDateOnly(new Date());
    data.overdue = 0;
    data.id = null;
    data.invoiceNumber = null;

    return [initialize(LIST_EDIT_VIEW_FORM_NAME, data)];
  },
  processError: response => [...FetchErrorHandler(response, "Failed to duplicate Invoice")]
};

export const EpicDuplicateAndReverseInvoice: Epic<any, any> = EpicUtils.Create(request);
