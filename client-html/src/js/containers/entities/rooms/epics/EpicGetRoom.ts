/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { Room } from "@api/model";
import { initialize } from "redux-form";
import { clearActionsQueue } from "../../../../common/actions";
import { getNoteItems } from "../../../../common/components/form/notes/actions";
import * as EpicUtils from "../../../../common/epics/EpicUtils";
import { GET_ROOM_ITEM, GET_ROOM_ITEM_FULFILLED } from "../actions";
import { SET_LIST_EDIT_RECORD } from "../../../../common/components/list-view/actions";
import FetchErrorHandler from "../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { getEntityItemById } from "../../common/entityItemsService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../common/components/list-view/constants";

const request: EpicUtils.Request = {
  type: GET_ROOM_ITEM,
  getData: (id: number) => getEntityItemById("Room", id),
  processData: (room: Room, s, id) => [
      {
        type: GET_ROOM_ITEM_FULFILLED,
        payload: { room }
      },
      {
        type: SET_LIST_EDIT_RECORD,
        payload: { editRecord: room, name: room.name }
      },
      getNoteItems("Room", id, LIST_EDIT_VIEW_FORM_NAME),
      initialize(LIST_EDIT_VIEW_FORM_NAME, room),
      ...(s.actionsQueue.queuedActions.length ? [clearActionsQueue()] : [])
    ],
  processError: response => [...FetchErrorHandler(response), initialize(LIST_EDIT_VIEW_FORM_NAME, null)]
};

export const EpicGetRoom: Epic<any, any> = EpicUtils.Create(request);
