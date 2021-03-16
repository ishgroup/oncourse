/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_WAITING_LIST_ITEM, GET_WAITING_LIST_ITEM_FULFILLED } from "../actions/index";
import { WaitingList } from "@api/model";
import { SET_LIST_EDIT_RECORD } from "../../../../common/components/list-view/actions/index";
import { initialize } from "redux-form";
import WaitingListService from "../services/WaitingListService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request = {
  type: GET_WAITING_LIST_ITEM,
  getData: (id: number) => WaitingListService.getWaitingList(id),
  processData: (waitingList: WaitingList) => {
    return [
      {
        type: GET_WAITING_LIST_ITEM_FULFILLED
      },
      {
        type: SET_LIST_EDIT_RECORD,
        payload: { editRecord: waitingList, name: waitingList.courseName }
      },
      initialize(LIST_EDIT_VIEW_FORM_NAME, waitingList)
    ];
  }
};

export const EpicGetWaitingList: Epic<any, any> = EpicUtils.Create(request);
