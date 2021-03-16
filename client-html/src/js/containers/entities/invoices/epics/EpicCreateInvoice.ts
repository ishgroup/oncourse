/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import InvoiceService from "../services/InvoiceService";
import { CREATE_INVOICE_ITEM, CREATE_INVOICE_ITEM_FULFILLED } from "../actions/index";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { initialize } from "redux-form";
import { Invoice } from "@api/model";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";
import { preformatInvoice } from "../utils";

let savedItem: Invoice;

const request: EpicUtils.Request = {
  type: CREATE_INVOICE_ITEM,
  getData: payload => {
    savedItem = payload.invoice;
    return InvoiceService.createInvoice(preformatInvoice(payload.invoice));
  },
  processData: () => {
    return [
      {
        type: CREATE_INVOICE_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Invoice Record created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Invoice", listUpdate: true }
      },
      setListSelection([]),
      clearListNestedEditRecord(0),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ];
  },
  processError: response => [
    ...FetchErrorHandler(response, "Invoice Record was not created"),
    initialize(LIST_EDIT_VIEW_FORM_NAME, savedItem)
  ]
};

export const EpicCreateInvoice: Epic<any, any> = EpicUtils.Create(request);
