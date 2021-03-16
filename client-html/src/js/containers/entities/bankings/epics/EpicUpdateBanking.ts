/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { Banking } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_BANKING_ITEM, UPDATE_BANKING_ITEM, UPDATE_BANKING_ITEM_FULFILLED } from "../actions";
import { FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import { updateEntityItemById } from "../../common/entityItemsService";

const request: EpicUtils.Request<any, { id: number; banking: Banking }> = {
  type: UPDATE_BANKING_ITEM,
  getData: ({ id, banking }) => updateEntityItemById("Banking", id, banking),
  processData: (v, s, { id }) => [
      {
        type: UPDATE_BANKING_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Banking Record updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Banking", listUpdate: true, savedID: id }
      },
      ...s.list.fullScreenEditView ? [{
        type: GET_BANKING_ITEM,
        payload: id
      }] : []
    ],
  processError: response => [...FetchErrorHandler(response)]
};

export const EpicUpdateBanking: Epic<any, any> = EpicUtils.Create(request);
