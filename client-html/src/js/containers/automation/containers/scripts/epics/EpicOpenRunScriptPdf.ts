import { stopSubmit } from "redux-form";
import { Epic } from "redux-observable";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import { CONTEXT } from "../../../../../common/api/Constants";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { OPEN_RUN_SCRIPT_PDF, OPEN_RUN_SCRIPT_PDF_FULFILLED } from "../actions";

const request: EpicUtils.Request = {
  type: OPEN_RUN_SCRIPT_PDF,
  hideLoadIndicator: true,
  getData: ({ processId }) => {
    window.open(`${CONTEXT}v1/list/entity/script/execute/pdf/${processId}`);
    return Promise.resolve();
  },
  processData: () => [
    stopSubmit("ExecuteScriptForm"),
    { type: OPEN_RUN_SCRIPT_PDF_FULFILLED },
    { type: FETCH_SUCCESS, payload: { message: "Script execution has finished" } }
  ],
  processError: response => [stopSubmit("ExecuteScriptForm"), ...FetchErrorHandler(response)]
};

export const EpicOpenRunScriptPdf: Epic<any, any> = EpicUtils.Create(request);
