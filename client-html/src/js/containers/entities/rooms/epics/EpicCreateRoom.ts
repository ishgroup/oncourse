/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { CREATE_ROOM_ITEM, CREATE_ROOM_ITEM_FULFILLED } from "../actions/index";
import { Room } from "@api/model";
import RoomService from "../services/RoomService";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import { initialize } from "redux-form";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import {
  clearListNestedEditRecord,
  GET_RECORDS_REQUEST,
  setListSelection
} from "../../../../common/components/list-view/actions";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

let savedItem: Room;

const request: EpicUtils.Request = {
  type: CREATE_ROOM_ITEM,
  getData: payload => {
    savedItem = payload.room;

    return RoomService.createRoom(payload.room);
  },
  processData: () => {
    return [
      {
        type: CREATE_ROOM_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "New room created" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Room" }
      },
      setListSelection([]),
      clearListNestedEditRecord(0),
      initialize(LIST_EDIT_VIEW_FORM_NAME, null)
    ];
  },
  processError: response => [
    ...FetchErrorHandler(response, "Room was not created"),
    initialize(LIST_EDIT_VIEW_FORM_NAME, savedItem)
  ]
};

export const EpicCreateRoom: Epic<any, any> = EpicUtils.Create(request);
