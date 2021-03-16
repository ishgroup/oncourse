/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { DELETE_ROOM_ITEM, DELETE_ROOM_ITEM_FULFILLED } from "../actions/index";
import RoomService from "../services/RoomService";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import { initialize } from "redux-form";
import { GET_RECORDS_REQUEST, setListSelection } from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request = {
  type: DELETE_ROOM_ITEM,
  getData: (id: number) => RoomService.validateRemoveRoom(id),
  retrieveData: (id: number) => RoomService.removeRoom(id),
  processData: () => {
    return [
      {
        type: DELETE_ROOM_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Room deleted" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Room", listUpdate: true }
      },
      setListSelection([]),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ];
  },
  processError: response => FetchErrorHandler(response, "Room was not deleted")
};

export const EpicDeleteRoom: Epic<any, any> = EpicUtils.Create(request);
