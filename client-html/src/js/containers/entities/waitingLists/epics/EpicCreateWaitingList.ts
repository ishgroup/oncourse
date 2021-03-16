/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { WaitingList } from "@api/model";
import { initialize } from "redux-form";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { processCustomFields } from "../../customFieldTypes/utils";
import { CREATE_WAITING_LIST_ITEM, CREATE_WAITING_LIST_ITEM_FULFILLED } from "../actions";
import { FETCH_SUCCESS } from "../../../../common/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../../common/components/list-view/actions";
import WaitingListService from "../services/WaitingListService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request<any, { waitingList: WaitingList }> = {
  type: CREATE_WAITING_LIST_ITEM,
  getData: ({ waitingList }) => {
    processCustomFields(waitingList);
    return WaitingListService.createWaitingList(waitingList);
  },
  processData: () => [
      {
        type: CREATE_WAITING_LIST_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "New Waiting List created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "WaitingList" }
      },
      setListSelection([]),
      clearListNestedEditRecord(0),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ],
  processError: (response, { waitingList }) => [
    ...FetchErrorHandler(response, "Waiting List was not created"),
    initialize(LIST_EDIT_VIEW_FORM_NAME, waitingList)
  ]
};

export const EpicCreateWaitingList: Epic<any, any> = EpicUtils.Create(request);
