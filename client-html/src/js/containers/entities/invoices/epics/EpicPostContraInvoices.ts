/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { POST_CONTRA_INVOICES, POST_CONTRA_INVOICES_FULFILLED } from "../actions";
import { Invoice } from "@api/model";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import InvoiceService from "../services/InvoiceService";
import { FETCH_SUCCESS } from "../../../../common/actions";

const request: EpicUtils.Request<any, { id: number; invoicesToPay: number[] }> = {
  type: POST_CONTRA_INVOICES,
  getData: ({ id, invoicesToPay }) => InvoiceService.postContraInvoices(id, invoicesToPay),
  processData: () => {
    return [
      {
        type: POST_CONTRA_INVOICES_FULFILLED
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "AbstractInvoice", listUpdate: true }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Invoice owing updated" }
      }
    ];
  }
};

export const EpicPostContraInvoices: Epic<any, any> = EpicUtils.Create(request);
