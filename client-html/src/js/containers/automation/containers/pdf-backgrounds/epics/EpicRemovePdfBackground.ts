/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { getAutomationPdfBackgroundsList, REMOVE_AUTOMATION_PDF_BACKGROUND } from "../actions/index";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import ReportOverlayService from "../services/ReportOverlayService";

const request: EpicUtils.Request<any, number> = {
  type: REMOVE_AUTOMATION_PDF_BACKGROUND,
  getData: id => ReportOverlayService.removeReportOverlay(id),
  processData: () => [getAutomationPdfBackgroundsList(true)],
  processError: response => FetchErrorHandler(response, "Failed to delete PDF background")
};

export const EpicRemovePdfBackground: Epic<any, any> = EpicUtils.Create(request);
