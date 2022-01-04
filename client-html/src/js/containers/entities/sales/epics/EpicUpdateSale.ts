/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { Note, ProductItem } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_SALE, UPDATE_SALE, UPDATE_SALE_FULFILLED } from "../actions";
import { clearActionsQueue, FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import { updateEntityItemById } from "../../common/entityItemsService";
import { processCustomFields } from "../../customFieldTypes/utils";
import { processNotesAsyncQueue } from "../../../../common/components/form/notes/utils";

const request: EpicUtils.Request<any, { id: string; productItem: ProductItem & { notes: Note[] } }> = {
  type: UPDATE_SALE,
  getData: ({ id, productItem }) => {
    delete productItem.notes;
    processCustomFields(productItem);
    return updateEntityItemById("Sale", Number(id), productItem);
  },
  retrieveData: (p, s) => processNotesAsyncQueue(s.actionsQueue.queuedActions),
  processData: (v, s, { id }) => [
    ...(s.actionsQueue.queuedActions.length ? [clearActionsQueue()] : []),
      {
        type: UPDATE_SALE_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Sale Record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "ProductItem", listUpdate: true, savedID: id }
      },
      ...s.list.fullScreenEditView || s.list.records.layout === "Three column" ? [{
        type: GET_SALE,
        payload: { id }
      }] : []
    ],
  processError: response => FetchErrorHandler(response, "Sale Record was not updated")
};

export const EpicUpdateSale: Epic<any, any> = EpicUtils.Create(request);
