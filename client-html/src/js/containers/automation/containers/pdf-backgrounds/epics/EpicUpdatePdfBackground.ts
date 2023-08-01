/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { showMessage } from "../../../../../common/actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import {
  GET_AUTOMATION_PDF_BACKGROUND,
  getAutomationPdfBackgroundsList,
  UPDATE_AUTOMATION_PDF_BACKGROUND
} from "../actions";
import ReportOverlayService from "../services/ReportOverlayService";

const request: EpicUtils.Request<any, { fileName: string; id: number; overlay: File }> = {
  type: UPDATE_AUTOMATION_PDF_BACKGROUND,
  getData: ({ fileName, id, overlay }) => ReportOverlayService.updateReportOverlay(fileName, id, overlay),
  processData: (r, s, { id }) => [
      showMessage({ success: true, message: "PDF background updated" }),
      getAutomationPdfBackgroundsList(),
      {
        type: GET_AUTOMATION_PDF_BACKGROUND,
        payload: id
      }
    ],
  processError: response => FetchErrorHandler(response, "Failed to update PDF background")
};

export const EpicUpdatePdfBackground: Epic<any, any> = EpicUtils.Create(request);
