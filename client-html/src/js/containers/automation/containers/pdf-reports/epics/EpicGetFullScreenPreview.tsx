/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import * as React from "react";
import { Epic } from "redux-observable";
import { showConfirm } from "../../../../../common/actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { SHOW_REPORT_FULL_SCREEN_PREVIEW } from "../actions";
import PdfService from "../services/PdfService";

const request: EpicUtils.Request<any, number> = {
  type: SHOW_REPORT_FULL_SCREEN_PREVIEW,
  getData: id => PdfService.getHighQualityPreview(id).then(preview => {
    return new Promise((resolve) => {
      const reader = new FileReader();
      reader.onloadend = () => resolve(reader.result);
      reader.readAsDataURL(preview);
    });
  }),
  processData: data => [
    showConfirm({
      title: null,
      confirmMessage: <img
        alt="FullPreview"
        src={`data:image/png;base64, ${data.replace("data:application/pdf;base64,","")}`}
      />,
      onConfirm: null,
      cancelButtonText: "Close"
    })
  ],
  processError: response => FetchErrorHandler(response, "Failed to get PDF preview")
};

export const EpicGetFullScreenPreview: Epic<any, any> = EpicUtils.Create(request);
