/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { initialize } from "redux-form";
import { Script } from "@api/model";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import ScriptsService from "../services/ScriptsService";
import { FETCH_SUCCESS } from "../../../../../common/actions/index";
import { GET_SCRIPTS_LIST, POST_SCRIPT_ENTITY_FULFILLED, POST_SCRIPT_ENTITY_REQUEST } from "../actions/index";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { appendComponents } from "../utils/index";
import { SCRIPT_EDIT_VIEW_FORM_NAME } from "../constants";

const request: EpicUtils.Request<any, { script: Script }> = {
  type: POST_SCRIPT_ENTITY_REQUEST,
  getData: payload => ScriptsService.createScriptItem(appendComponents(payload.script)),
  processData: (r, s, { script }) => [
      {
        type: POST_SCRIPT_ENTITY_FULFILLED
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "New Script created" }
      },
      {
        type: GET_SCRIPTS_LIST,
        payload: { nameToSelect: script.name }
      },
      initialize(SCRIPT_EDIT_VIEW_FORM_NAME, script)
    ],
  processError: response => FetchErrorHandler(response, "Script was not created")
};

export const EpicCreateScriptItem: Epic<any, any> = EpicUtils.Create(request);
