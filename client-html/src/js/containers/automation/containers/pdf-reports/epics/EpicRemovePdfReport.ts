/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { REMOVE_AUTOMATION_PDF_REPORT, getAutomationPdfReportsList } from "../actions/index";
import { State } from "../../../../../reducers/state";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import PdfService from "../services/PdfService";

const request: EpicUtils.Request<any, State, number> = {
  type: REMOVE_AUTOMATION_PDF_REPORT,
  getData: id => PdfService.removeReport(id),
  processData: () => {
    return [getAutomationPdfReportsList(true)];
  },
  processError: response => FetchErrorHandler(response, "Failed to delete PDF report")
};

export const EpicRemovePdfReport: Epic<any, any> = EpicUtils.Create(request);
