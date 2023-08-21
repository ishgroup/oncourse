/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
import { stopSubmit } from "redux-form";
import { Epic } from "redux-observable";
import { START_PROCESS, UPDATE_PROCESS } from "../../../../../common/actions";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import {
  getRunScriptResult,
  openRunScriptPdf,
  POST_SCRIPT_RUN_REQUEST,
  POST_SCRIPT_RUN_REQUEST_FULFILLED
} from "../actions";
import ScriptsService from "../services/ScriptsService";

const request: EpicUtils.Request<any, any> = {
  type: POST_SCRIPT_RUN_REQUEST,
  hideLoadIndicator: true,
  getData: ({ executeScriptRequest }) => ScriptsService.runScript(executeScriptRequest),
  processData: (processId: string, state, { outputType, name }) => [
    { type: POST_SCRIPT_RUN_REQUEST_FULFILLED },
    {
      type: UPDATE_PROCESS,
      payload: { processId }
    },
    {
      type: START_PROCESS,
      payload: {
        processId,
        actionsOnFail: [stopSubmit("ExecuteScriptForm")],
        actions: [
          outputType === "pdf" ? openRunScriptPdf(processId) : getRunScriptResult(processId, outputType, name)
        ]
      }
    }
  ]
};

export const EpicRunScriptItem: Epic<any, any> = EpicUtils.Create(request);
