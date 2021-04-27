/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { ExportTemplate } from "@api/model";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { CREATE_EXPORT_TEMPLATE, CREATE_EXPORT_TEMPLATE_FULFILLED, GET_EXPORT_TEMPLATES_LIST } from "../actions/index";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import ExportTemplatesService from "../services/ExportTemplatesService";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import { State } from "../../../../../reducers/state";

const request: EpicUtils.Request<State, { exportTemplate: ExportTemplate }> = {
  type: CREATE_EXPORT_TEMPLATE,
  getData: ({ exportTemplate }) => ExportTemplatesService.create(exportTemplate),
  processData: (v, s, { exportTemplate }) => [
      {
        type: CREATE_EXPORT_TEMPLATE_FULFILLED
      },
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
