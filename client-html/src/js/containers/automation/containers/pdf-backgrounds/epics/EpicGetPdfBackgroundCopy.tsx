/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { GET_PDF_BACKGROUND_COPY, getPdfBackgroundCopyListFulfilled } from "../actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import ReportOverlayService from "../services/ReportOverlayService";
import { createAndDownloadFile } from "../../../../../common/utils/common";

const request: EpicUtils.Request<any, { id: number, name: string }> = {
  type: GET_PDF_BACKGROUND_COPY,
  hideLoadIndicator: true,
  getData: ({ id }) => ReportOverlayService.getReportOverlayCopy(id),
  processData: (r, s, { name }) => {

    r.forEach(f => {
      createAndDownloadFile(f, "png", name);
    });
    
    return [
      getPdfBackgroundCopyListFulfilled(true)
    ];
  },
  processError: response => [
    ...FetchErrorHandler(response, "Failed to get PDF background copy"),
    getPdfBackgroundCopyListFulfilled(false)
  ]
};

export const EpicGetPdfBackgroundCopy: Epic<any, any> = EpicUtils.Create(request);