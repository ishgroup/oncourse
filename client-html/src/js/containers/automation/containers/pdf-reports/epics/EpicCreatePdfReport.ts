/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { CREATE_AUTOMATION_PDF_REPORT, getAutomationPdfReportsList } from "../actions/index";
import { State } from "../../../../../reducers/state";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import PdfService from "../services/PdfService";
import { Report } from "@api/model";

const request: EpicUtils.Request<any, State, { report: Report }> = {
  type: CREATE_AUTOMATION_PDF_REPORT,
  getData: ({ report }) => PdfService.createReport(report),
  processData: (r, s, p) => {
    return [getAutomationPdfReportsList(false, p.report.keyCode)];
  },
  processError: response => FetchErrorHandler(response, "Failed to create PDF report")
};

export const EpicCreatePdfReport: Epic<any, any> = EpicUtils.Create(request);
