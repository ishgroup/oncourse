/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ExportTemplate } from "@api/model";
import { initialize } from "redux-form";
import { Epic } from "redux-observable";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { State } from "../../../../../reducers/state";
import { CREATE_EXPORT_TEMPLATE, GET_EXPORT_TEMPLATES_LIST } from "../actions";
import { EXPORT_TEMPLATES_FORM_NAME } from "../ExportTemplates";
import ExportTemplatesService from "../services/ExportTemplatesService";

const request: EpicUtils.Request<State, { exportTemplate: ExportTemplate }> = {
  type: CREATE_EXPORT_TEMPLATE,
  getData: ({ exportTemplate }) => ExportTemplatesService.create(exportTemplate),
  processData: (v, s, { exportTemplate }) => [
      initialize(EXPORT_TEMPLATES_FORM_NAME, exportTemplate),
      {
        type: GET_EXPORT_TEMPLATES_LIST,
        payload: { keyCodeToSelect: exportTemplate.keyCode }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Export template created" }
      }
    ],
  processError: response => FetchErrorHandler(response, "Failed to create export template")
};

export const EpicCreateExportTemplate: Epic<any, any> = EpicUtils.Create(request);
