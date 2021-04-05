/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import {
  GET_SCRIPT_ENTITY_REQUEST,
  GET_SCRIPTS_LIST,
  UPDATE_SCRIPT_ENTITY_REQUEST,
  UPDATE_SCRIPT_ENTITY_REQUEST_FULFILLED,
} from "../actions";
import { updateEntityItemById } from "../../../../entities/common/entityItemsService";

const request: EpicUtils.Request = {
  type: UPDATE_SCRIPT_ENTITY_REQUEST,
  getData: ({ id, script, method }) => updateEntityItemById("Script", id, script, method),
  processData: (v, s, { id }) => [
    {
      type: UPDATE_SCRIPT_ENTITY_REQUEST_FULFILLED,
    },
    {
      type: GET_SCRIPTS_LIST,
    },
    {
      type: FETCH_SUCCESS,
      payload: { message: "Script was updated" },
    },
    {
      type: GET_SCRIPT_ENTITY_REQUEST,
      payload: id,
    },
  ],
};

export const EpicSaveScriptItem: Epic<any, any> = EpicUtils.Create(request);
