/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ExportTemplate, ImportModel } from "@api/model";
import { Epic } from "redux-observable";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { GET_EXPORT_TEMPLATE, GET_EXPORT_TEMPLATES_LIST, UPDATE_EXPORT_TEMPLATE } from "../actions";
import ExportTemplatesService from "../services/ExportTemplatesService";

const request: EpicUtils.Request<{ importTemplate: ImportModel }, { exportTemplate: ExportTemplate }> = {
  type: UPDATE_EXPORT_TEMPLATE,
  getData: ({ exportTemplate }) => ExportTemplatesService.update(exportTemplate.id, exportTemplate),
  processData: (v, s, { exportTemplate: { id } }) => [
      {
        type: GET_EXPORT_TEMPLATE,
        payload: id
      },
      {
        type: GET_EXPORT_TEMPLATES_LIST
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Export template updated" }
      }
    ],
  processError: response => FetchErrorHandler(response, "Failed to update  export template")
};

export const EpicUpdateExportTemplate: Epic<any, any> = EpicUtils.Create(request);
