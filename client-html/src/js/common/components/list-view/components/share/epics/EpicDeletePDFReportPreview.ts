/*
 * Copyright ish group pty ltd 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import * as EpicUtils from "../../../../../epics/EpicUtils";
import { DELETE_PDF_REPORT_PREVIEW, getShareList } from "../actions";
import FetchErrorHandler from "../../../../../api/fetch-errors-handlers/FetchErrorHandler";
import { FETCH_SUCCESS } from "../../../../../actions";
import PdfService from "../../../../../../containers/automation/containers/pdf-reports/services/PdfService";

const request: EpicUtils.Request<any, number> = {
  type: DELETE_PDF_REPORT_PREVIEW,
  getData: id => PdfService.deletePreview(id),
  processData: (value, {list: {records: {entity}}}) => ([
    getShareList(entity),
    {
      type: FETCH_SUCCESS,
      payload: {message: "Preview was deleted"}
    }
  ]),
  processError: response => FetchErrorHandler(response, "Failed to delete preview")
};

export const EpicDeletePDFReportPreview: Epic<any, any> = EpicUtils.Create(request);