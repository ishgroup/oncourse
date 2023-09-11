/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { PrintRequest } from "@api/model";
import { stopSubmit } from "redux-form";
import { Epic } from "redux-observable";
import PdfService from "../../../../../../containers/automation/containers/pdf-reports/services/PdfService";
import { clearProcess, showMessage, START_PROCESS, UPDATE_PROCESS } from "../../../../../actions";
import FetchErrorHandler from "../../../../../api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import { DO_PRINT_REQUEST, DO_PRINT_REQUEST_FULFILLED, GET_PDF_REPORTS, GET_PRINT_RESULT } from "../actions";
import { LIST_SHARE_FORM_NAME } from "../constants";

const request: EpicUtils.Request<any, { rootEntity: string, printRequest: PrintRequest }> = {
  type: DO_PRINT_REQUEST,
  hideLoadIndicator: true,
  getData: payload => PdfService.doPrint(payload.rootEntity, payload.printRequest),
  processData: (processId: string, state, { rootEntity, printRequest }) => [
    {
      type: DO_PRINT_REQUEST_FULFILLED
    },
    ...printRequest.emailToSent
      ? [
        stopSubmit(LIST_SHARE_FORM_NAME),
        showMessage({ message: "Print request has been sent. Result will be sent to the specified email", success: true })
      ]
      : [
      {
        type: UPDATE_PROCESS,
        payload: { processId }
      },
      {
        type: START_PROCESS,
        payload: {
          processId,
          actionsOnFail: [stopSubmit(LIST_SHARE_FORM_NAME)],
          actions: [
            {
              type: GET_PRINT_RESULT,
              payload: { entityName: rootEntity, processId }
            },
            ...(printRequest.createPreview
              ? [
                {
                  type: GET_PDF_REPORTS,
                  payload: rootEntity
                }
              ]
              : [])
          ]
        }
      }
    ],
  ],
  processError: response => [
    clearProcess(),
    ...FetchErrorHandler(response)
  ]
};

export const EpicStartPrintProcess: Epic<any, any> = EpicUtils.Create(request);
