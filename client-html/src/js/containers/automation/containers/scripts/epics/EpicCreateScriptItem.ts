/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { initialize } from "redux-form";
import { Epic } from "redux-observable";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { GET_SCRIPTS_LIST, POST_SCRIPT_ENTITY_FULFILLED, POST_SCRIPT_ENTITY_REQUEST } from "../actions";
import { SCRIPT_EDIT_VIEW_FORM_NAME } from "../constants";
import ScriptsService from "../services/ScriptsService";
import { appendComponents } from "../utils";

const request: EpicUtils.Request = {
  type: POST_SCRIPT_ENTITY_REQUEST,
  getData: ({ script, viewMode }) => {
    if (viewMode === "Cards" && script.savedQuerypPrefixes?.length && script.components.length) {
      script.components.forEach(component => {
        if (component.type === "Query") {
          const currentPrefix = script.savedQuerypPrefixes.filter(prefix => prefix.id === component.id)[0];
          if (currentPrefix) {
            if (currentPrefix.prefixValue.includes("def") && component.queryClosureReturnValue.includes("def")) {
              currentPrefix.prefixValue.replace("def", "");
            }
            if (currentPrefix.prefixValue) {
              component.queryClosureReturnValue = currentPrefix.prefixValue + " " + component.queryClosureReturnValue;
            }
          }
        }
      });
    }

    if (script.queryClosureReturnValue) delete script.queryClosureReturnValue;
    if (script.savedQuerypPrefixes) delete script.savedQuerypPrefixes;
    
    return ScriptsService.createScriptItem(appendComponents(script, viewMode));
  },
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
