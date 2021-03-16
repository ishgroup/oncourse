/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { initialize } from "redux-form";
import { ReportOverlay } from "@api/model";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { GET_AUTOMATION_PDF_BACKGROUND } from "../actions";
import { PDF_BACKGROUND_FORM_NAME } from "../PdfBackgrounds";
import ReportOverlayService from "../services/ReportOverlayService";

const request: EpicUtils.Request<any, number> = {
  type: GET_AUTOMATION_PDF_BACKGROUND,
  getData: id => ReportOverlayService.getReportOverlay(id),
  processData: (report: ReportOverlay) => [initialize(PDF_BACKGROUND_FORM_NAME, report)],
  processError: response => [
    ...FetchErrorHandler(response, "Failed to get PDF background"),
    initialize(PDF_BACKGROUND_FORM_NAME, null)
  ]
};

export const EpicGetPdfBackground: Epic<any, any> = EpicUtils.Create(request);
