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
  UPDATE_SCRIPT_ENTITY_REQUEST_FULFILLED
} from "../actions";
import { updateEntityItemById, updateEntityItemByIdErrorHandler } from "../../../../entities/common/entityItemsService";
import { LIST_EDIT_VIEW_FORM_NAME } from "../../../../../common/components/list-view/constants";

const request: EpicUtils.Request<any, any> = {
  type: UPDATE_SCRIPT_ENTITY_REQUEST,
  getData: ({ id, script, method }) => updateEntityItemById("Script", id, script, method),
  processData: (v, s, { id }) => [
    {
      type: UPDATE_SCRIPT_ENTITY_REQUEST_FULFILLED
    },
    {
      type: GET_SCRIPTS_LIST
    },
    {
      type: FETCH_SUCCESS,
      payload: { message: "Script was updated" }
    },
    {
      type: GET_SCRIPT_ENTITY_REQUEST,
      payload: id
    }
  ],
  processError: (response, { script }) =>
    updateEntityItemByIdErrorHandler(response, "Script", LIST_EDIT_VIEW_FORM_NAME, script)
};

export const EpicSaveScriptItem: Epic<any, any> = EpicUtils.Create(request);
