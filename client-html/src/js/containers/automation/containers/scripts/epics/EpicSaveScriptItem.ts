/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import {
  GET_SCRIPT_ENTITY_REQUEST,
  GET_SCRIPTS_LIST,
  UPDATE_SCRIPT_ENTITY_REQUEST,
  UPDATE_SCRIPT_ENTITY_REQUEST_FULFILLED,
} from "../actions";
import ScriptsService from "../services/ScriptsService";
import { appendComponents } from "../utils";

const request: EpicUtils.Request = {
  type: UPDATE_SCRIPT_ENTITY_REQUEST,
  getData: ({
   id, script, method, viewMode
  }) => {
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

    if (method === "PATCH") {
      return ScriptsService.patchScriptItem(id, appendComponents(script, viewMode));
    }
    return ScriptsService.saveScriptItem(id, appendComponents(script, viewMode));
  },
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
