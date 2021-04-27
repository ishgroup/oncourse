/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { Script } from "@api/model";
import { initialize } from "redux-form";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { GET_SCRIPT_ENTITY_FULFILLED, GET_SCRIPT_ENTITY_REQUEST } from "../actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { SCRIPT_EDIT_VIEW_FORM_NAME } from "../constants";
import ScriptsService from "../services/ScriptsService";
import { ParseScriptBody } from "../utils";

const request: EpicUtils.Request = {
  type: GET_SCRIPT_ENTITY_REQUEST,
  getData: id => ScriptsService.getScriptItem(id).then(response => ParseScriptBody(response)),
  processData: (editRecord: Script) => [
    { type: GET_SCRIPT_ENTITY_FULFILLED },
    initialize(SCRIPT_EDIT_VIEW_FORM_NAME, editRecord)
  ],
  processError: response => [...FetchErrorHandler(response, "Failed to get script"), initialize(SCRIPT_EDIT_VIEW_FORM_NAME, null)]
};

export const EpicGetScriptItem: Epic<any, any> = EpicUtils.Create(request);
