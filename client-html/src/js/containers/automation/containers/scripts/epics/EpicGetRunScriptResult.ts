/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { stopSubmit } from "redux-form";
import { Epic } from "redux-observable";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { createAndDownloadFile } from "../../../../../common/utils/common";
import { GET_RUN_SCRIPT_RESULT, GET_RUN_SCRIPT_RESULT_FULFILLED } from "../actions";
import ScriptsService from "../services/ScriptsService";

const getExtensionByContentType = contentType => {
  switch (contentType) {
    case "text/csv": {
      return "csv";
    }
    case "application/json": {
      return "json";
    }
    case "application/xml": {
      return "xml";
    }
    case "text/calendar": {
      return "ics";
    }
    case "text/plain": {
      return "txt";
    }
    case "application/pdf": {
      return "pdf";
    }
    default: {
      return "";
    }
  }
};

const request: EpicUtils.Request = {
  type: GET_RUN_SCRIPT_RESULT,
  hideLoadIndicator: true,
  getData: ({ processId }) => ScriptsService.getRunScriptResultResponse(processId),
  processData: ({ headers, data }: any, state, { name }) => {
    const contentType = headers["content-type"];
    const outputType = getExtensionByContentType(contentType);

    if (data) {
      createAndDownloadFile(data, outputType, name || "");
    }

    return [
      stopSubmit("ExecuteScriptForm"),
      { type: GET_RUN_SCRIPT_RESULT_FULFILLED },
      { type: FETCH_SUCCESS, payload: { message: "Script execution has finished" } }
    ];
  },
  processError: response => [stopSubmit("ExecuteScriptForm"), ...FetchErrorHandler(response, "Error. Script was not executed.")]
};

export const EpicGetRunScriptResult: Epic<any, any> = EpicUtils.Create(request);
