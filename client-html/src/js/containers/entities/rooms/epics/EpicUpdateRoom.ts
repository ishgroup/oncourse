/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { Room } from "@api/model";
import { initialize } from "redux-form";
import { processNotesAsyncQueue } from "../../../../common/components/form/notes/utils";

import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_ROOM_ITEM, UPDATE_ROOM_ITEM, UPDATE_ROOM_ITEM_FULFILLED } from "../actions/index";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { FETCH_SUCCESS } from "../../../../common/actions/index";
import { GET_RECORDS_REQUEST } from "../../../../common/components/list-view/actions";
import { updateEntityItemById } from "../../common/entityItemsService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

let savedID: string;
let savedItem: Room;

const request: EpicUtils.Request = {
  type: UPDATE_ROOM_ITEM,
  getData: payload => {
    savedID = payload.id;
    savedItem = payload.room;
    delete payload.room.notes;

    return updateEntityItemById("Room", payload.id, payload.room);
  },
  retrieveData: (p, s) => processNotesAsyncQueue(s.actionsQueue.queuedActions),
  processData: (p, s) => [
      {
        type: UPDATE_ROOM_ITEM_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Room updated" }
      },
      {
        type: GET_RECORDS_REQUEST,
        payload: { entity: "Room", listUpdate: true, savedID }
      },
      ...s.list.fullScreenEditView || s.list.records.layout === "Three column" ? [{
        type: GET_ROOM_ITEM,
        payload: savedID
      }] : []
    ],
  processError: response => [...FetchErrorHandler(response, "Room was not updated"), initialize(LIST_EDIT_VIEW_FORM_NAME, savedItem)]
};

export const EpicUpdateRoom: Epic<any, any> = EpicUtils.Create(request);
