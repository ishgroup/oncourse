/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { initialize } from "redux-form";
import { WaitingList } from "@api/model";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { processCustomFields } from "../../customFieldTypes/utils";
import { GET_WAITING_LIST_ITEM, UPDATE_WAITING_LIST_ITEM, UPDATE_WAITING_LIST_ITEM_FULFILLED } from "../actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { FETCH_SUCCESS } from "../../../../common/actions";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import WaitingListService from "../services/WaitingListService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request<any, { id: number; waitingList: WaitingList }> = {
  type: UPDATE_WAITING_LIST_ITEM,
  getData: ({ id, waitingList }) => {
    processCustomFields(waitingList);
    return WaitingListService.updateWaitingList(id, waitingList);
  },
  processData: (v, s, { id }) => [
    {
      type: UPDATE_WAITING_LIST_ITEM_FULFILLED
    },
    {
      type: FETCH_SUCCESS,
      payload: { message: "Waiting List was updated" }
    },
    {
      type: GET_RECORDS_REQUEST,
      payload: { entity: "WaitingList", listUpdate: true, savedID: id }
    },
    ...s.list.fullScreenEditView ? [{
      type: GET_WAITING_LIST_ITEM,
      payload: id
    }] : []
  ],
  processError: (response, { waitingList }) => [
      ...FetchErrorHandler(response, "Waiting List was not updated"),
      initialize(LIST_EDIT_VIEW_FORM_NAME, waitingList)
    ]
};

export const EpicUpdateWaitingListItem: Epic<any, any> = EpicUtils.Create(request);
