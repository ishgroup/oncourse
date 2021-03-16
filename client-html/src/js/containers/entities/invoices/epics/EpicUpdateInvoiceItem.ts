/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { initialize } from "redux-form";
import { Invoice } from "@api/model";
import { processNotesAsyncQueue } from "../../../../common/components/form/notes/utils";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_INVOICE_ITEM, UPDATE_INVOICE_ITEM, UPDATE_INVOICE_ITEM_FULFILLED } from "../actions/index";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import { updateEntityItemById } from "../../common/entityItemsService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request<any, { id: number; invoice: Invoice & { notes: any } }> = {
  type: UPDATE_INVOICE_ITEM,
  getData: ({ id, invoice }) => {
    delete invoice.notes;
    return updateEntityItemById("Invoice", id, invoice);
  },
  retrieveData: (p, s) => processNotesAsyncQueue(s.actionsQueue.queuedActions),
  processData: (v, s, { id }) => [
      {
        type: UPDATE_INVOICE_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Invoice Record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Invoice", listUpdate: true, savedID: id }
      },
      ...s.list.fullScreenEditView ? [{
          type: GET_INVOICE_ITEM,
          payload: id
        }] : []
    ],
  processError: (response, { invoice }) => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, invoice)]
};

export const EpicUpdateInvoiceItem: Epic<any, any> = EpicUtils.Create(request);
