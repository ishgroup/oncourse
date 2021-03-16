/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { Report } from "@api/model";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { GET_AUTOMATION_PDF_REPORT, getAutomationPdfReportsList, UPDATE_AUTOMATION_PDF_REPORT } from "../actions/index";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import PdfService from "../services/PdfService";
import { FETCH_SUCCESS } from "../../../../../common/actions";

const request: EpicUtils.Request<any, { report: Report }> = {
  type: UPDATE_AUTOMATION_PDF_REPORT,
  getData: ({ report }) => PdfService.updateReport(report.id, report),
  processData: (r, s, { report }) => [
      {
        type: FETCH_SUCCESS,
        payload: { message: "PDF report updated" }
      },
      getAutomationPdfReportsList(),
      {
        type: GET_AUTOMATION_PDF_REPORT,
        payload: report.id
      }
    ],
  processError: response => FetchErrorHandler(response, "Failed to update PDF report")
};

export const EpicUpdatePdfReport: Epic<any, any> = EpicUtils.Create(request);
