/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { stopSubmit } from "redux-form";
import { Epic } from "redux-observable";
import PdfService from "../../../../../../containers/automation/containers/pdf-reports/services/PdfService";
import { clearProcess, START_PROCESS, UPDATE_PROCESS } from "../../../../../actions";
import FetchErrorHandler from "../../../../../api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import { DO_PRINT_REQUEST, DO_PRINT_REQUEST_FULFILLED, GET_PDF_REPORTS, GET_PRINT_RESULT } from "../actions";

const request: EpicUtils.Request = {
  type: DO_PRINT_REQUEST,
  hideLoadIndicator: true,
  getData: payload => PdfService.doPrint(payload.rootEntity, payload.printRequest),
  processData: (processId: string, state, payload) => [
    {
      type: DO_PRINT_REQUEST_FULFILLED
    },
    {
      type: UPDATE_PROCESS,
      payload: {processId}
    },
    {
      type: START_PROCESS,
      payload: {
        processId,
        actionsOnFail: [stopSubmit("ListShareForm")],
        actions: [
          {
            type: GET_PRINT_RESULT,
            payload: {entityName: payload.rootEntity, processId}
          },
          ...(payload.printRequest.createPreview
            ? [
              {
                type: GET_PDF_REPORTS,
                payload: payload.rootEntity
              }
            ]
            : [])
        ]
      }
    }
  ],
  processError: response => [
    clearProcess(),
    ...FetchErrorHandler(response)
  ]
};

export const EpicStartPrintProcess: Epic<any, any> = EpicUtils.Create(request);
