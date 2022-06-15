/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { Report } from "@api/model";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { CREATE_AUTOMATION_PDF_REPORT, getAutomationPdfReportsList } from "../actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import PdfService from "../services/PdfService";
import { initialize } from "redux-form";
import { PDF_REPORT_FORM_NAME } from "../PdfReports";

const request: EpicUtils.Request<any, { report: Report }> = {
  type: CREATE_AUTOMATION_PDF_REPORT,
  getData: ({ report }) => PdfService.createReport(report),
  processData: (r, s, p) => [
    initialize(PDF_REPORT_FORM_NAME, p.report),
    getAutomationPdfReportsList(false, p.report.keyCode)
  ],
  processError: response => FetchErrorHandler(response, "Failed to create PDF report")
};

export const EpicCreatePdfReport: Epic<any, any> = EpicUtils.Create(request);
