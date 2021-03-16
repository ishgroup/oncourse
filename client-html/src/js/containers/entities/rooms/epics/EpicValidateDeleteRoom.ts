/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_ROOM_DELETE_VALIDATION, GET_ROOM_DELETE_VALIDATION_FULFILLED } from "../actions/index";
import RoomService from "../services/RoomService";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";

const request: EpicUtils.Request<any, { id: number; callback: any }> = {
  type: GET_ROOM_DELETE_VALIDATION,
  getData: ({ id }) => RoomService.validateRemoveRoom(id),
  processData: (v, s, { callback }) => {
    callback();

    return [
      {
        type: GET_ROOM_DELETE_VALIDATION_FULFILLED
      }
    ];
  },
  processError: response => FetchErrorHandler(response)
};

export const EpicValidateDeleteRoom: Epic<any, any> = EpicUtils.Create(request);
