/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { Invoice } from "@api/model";
import { initialize } from "redux-form";
import { clearActionsQueue } from "../../../../common/actions";
import { getNoteItems } from "../../../../common/components/form/notes/actions";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_INVOICE_ITEM, GET_INVOICE_ITEM_FULFILLED } from "../actions";
import { SET_LIST_EDIT_RECORD } from "../../../../common/components/list-view/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import InvoiceService from "../services/InvoiceService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import { getInvoiceClosestPaymentDueDate, sortInvoicePaymentPlans } from "../utils";

const request: EpicUtils.Request = {
  type: GET_INVOICE_ITEM,
  getData: (id: number) => InvoiceService.getInvoice(id),
  processData: (invoice: Invoice, s, id) => {
    invoice.paymentPlans.sort(sortInvoicePaymentPlans);
    getInvoiceClosestPaymentDueDate(invoice);

    return [
      {
        type: GET_INVOICE_ITEM_FULFILLED,
        payload: { invoice }
      },
      {
        type: SET_LIST_EDIT_RECORD,
        payload: { editRecord: invoice, name: invoice.invoiceNumber }
      },
      getNoteItems("Invoice", id, LIST_EDIT_VIEW_FORM_NAME),
      initialize(LIST_EDIT_VIEW_FORM_NAME, invoice),
      ...(s.actionsQueue.queuedActions.length ? [clearActionsQueue()] : [])
    ];
  },
  processError: response => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, null)]
};

export const EpicGetInvoice: Epic<any, any> = EpicUtils.Create(request);
