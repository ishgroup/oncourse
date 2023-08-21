import { Epic } from "redux-observable";
import { FETCH_FAIL, FETCH_SUCCESS, START_PROCESS, UPDATE_PROCESS } from "../../../../../common/actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { POST_IMPORT_RUN_REQUEST, POST_IMPORT_RUN_REQUEST_FULFILLED } from "../actions";
import ImportTemplatesService from "../services/ImportTemplatesService";

const request: EpicUtils.Request<any, any> = {
  type: POST_IMPORT_RUN_REQUEST,
  hideLoadIndicator: true,
  getData: ({ executeImportRequest, files }) => ImportTemplatesService.execute(executeImportRequest, files),
  processData: (processId: string) => [
    { type: POST_IMPORT_RUN_REQUEST_FULFILLED, payload: processId },
    {
      type: UPDATE_PROCESS,
      payload: { processId }
    },
    {
      type: START_PROCESS,
      payload: {
        processId,
        actionsOnFail: [
          {
            type: FETCH_FAIL,
            payload: { message: "Failed to process import" }
          }
        ],
        actions: [
          {
            type: FETCH_SUCCESS,
            payload: { message: "Import successfully finished" }
          }
        ]
      }
    }
  ],
  processError: response => FetchErrorHandler(response, "Failed to process import")
};

export const EpicRunImportItem: Epic<any, any> = EpicUtils.Create(request);
