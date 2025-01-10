/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Report } from "@api/model";

import { initialize } from "redux-form";
import { Epic } from "redux-observable";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { GET_AUTOMATION_PDF_REPORT } from "../actions/index";
import { PDF_REPORT_FORM_NAME } from "../PdfReports";
import PdfService from "../services/PdfService";

const request: EpicUtils.Request<any, number> = {
  type: GET_AUTOMATION_PDF_REPORT,
  getData: id => PdfService.getReport(id),
  processData: (report: Report) => [initialize(PDF_REPORT_FORM_NAME, report)],
  processError: response => [
    ...FetchErrorHandler(response, "Failed to get PDF report"),
    initialize(PDF_REPORT_FORM_NAME, null)
  ]
};

export const EpicGetPdfReport: Epic<any, any> = EpicUtils.Create(request);
