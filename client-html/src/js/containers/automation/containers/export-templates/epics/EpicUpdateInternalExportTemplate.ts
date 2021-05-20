/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { ExportTemplate } from "@api/model";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import {
  GET_EXPORT_TEMPLATE,
  GET_EXPORT_TEMPLATES_LIST,
  UPDATE_INTERNAL_EXPORT_TEMPLATE,
  UPDATE_INTERNAL_EXPORT_TEMPLATE_FULFILLED
} from "../actions/index";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import ExportTemplatesService from "../services/ExportTemplatesService";
import { FETCH_SUCCESS } from "../../../../../common/actions";

const request: EpicUtils.Request<{ exportTemplate: ExportTemplate }, { exportTemplate: ExportTemplate }> = {
  type: UPDATE_INTERNAL_EXPORT_TEMPLATE,
  getData: ({ exportTemplate }) => ExportTemplatesService.updateInternal(exportTemplate),
  processData: (v, s, { exportTemplate: { id } }) => [
      {
        type: UPDATE_INTERNAL_EXPORT_TEMPLATE_FULFILLED
      },
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

export const EpicUpdateInternalExportTemplate: Epic<any, any> = EpicUtils.Create(request);
