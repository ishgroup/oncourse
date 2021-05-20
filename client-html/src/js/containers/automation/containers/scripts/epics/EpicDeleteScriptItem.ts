/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import ScriptsService from "../services/ScriptsService";
import { FETCH_SUCCESS } from "../../../../../common/actions/index";
import { DELETE_SCRIPT_ENTITY_FULFILLED, DELETE_SCRIPT_ENTITY_REQUEST, GET_SCRIPTS_LIST } from "../actions/index";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";

const request: EpicUtils.Request<any, any> = {
  type: DELETE_SCRIPT_ENTITY_REQUEST,
  getData: payload => ScriptsService.deleteScriptItem(payload.id),
  processData: () => [
      {
        type: GET_SCRIPTS_LIST,
        payload: { selectFirst: true }
      },
      {
        type: DELETE_SCRIPT_ENTITY_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Script was deleted" }
      }
    ],
  processError: response => FetchErrorHandler(response, "Script was not deleted")
};

export const EpicDeleteScriptItem: Epic<any, any> = EpicUtils.Create(request);
