/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import {
  UPDATE_INTERNAL_AUTOMATION_PDF_REPORT,
  getAutomationPdfReportsList,
  GET_AUTOMATION_PDF_REPORT
} from "../actions/index";
import { State } from "../../../../../reducers/state";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import PdfService from "../services/PdfService";
import { Report } from "@api/model";
import { FETCH_SUCCESS } from "../../../../../common/actions";

const request: EpicUtils.Request<any, State, { report: Report }> = {
  type: UPDATE_INTERNAL_AUTOMATION_PDF_REPORT,
  getData: ({ report }) => PdfService.updateInternalReport(report),
  processData: (r, s, { report }) => {
    return [
      {
        type: FETCH_SUCCESS,
        payload: { message: "PDF report updated" }
      },
      getAutomationPdfReportsList(),
      {
        type: GET_AUTOMATION_PDF_REPORT,
        payload: report.id
      }
    ];
  },
  processError: response => FetchErrorHandler(response, "Failed to update internal PDF report")
};

export const EpicUpdateInternalPdfReport: Epic<any, any> = EpicUtils.Create(request);
