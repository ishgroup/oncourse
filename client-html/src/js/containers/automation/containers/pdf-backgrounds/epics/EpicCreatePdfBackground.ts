/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { CREATE_AUTOMATION_PDF_BACKGROUND, getAutomationPdfBackgroundsList } from "../actions/index";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import ReportOverlayService from "../services/ReportOverlayService";
import { FETCH_SUCCESS } from "../../../../../common/actions";

const request: EpicUtils.Request<any, { fileName: string; overlay: File }> = {
  type: CREATE_AUTOMATION_PDF_BACKGROUND,
  getData: ({ fileName, overlay }) => ReportOverlayService.addOverlay(fileName, overlay),
  processData: (r, s, p) => [
      getAutomationPdfBackgroundsList(false, p.fileName),
      {
        type: FETCH_SUCCESS,
        payload: { message: "New Background was added" }
      }
    ],
  processError: response => FetchErrorHandler(response, "Failed to create PDF background")
};

export const EpicCreatePdfBackground: Epic<any, any> = EpicUtils.Create(request);
