/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import history from "../../../../../constants/History";
import { GET_EXPORT_TEMPLATES_LIST, REMOVE_EXPORT_TEMPLATE } from "../actions";
import ExportTemplatesService from "../services/ExportTemplatesService";

const request: EpicUtils.Request<any, number> = {
  type: REMOVE_EXPORT_TEMPLATE,
  getData: id => ExportTemplatesService.remove(id),
  processData: () => {
    history.push("/automation/export-templates");

    return [
      {
        type: GET_EXPORT_TEMPLATES_LIST
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Export template deleted" }
      }
    ];
  },
  processError: response => FetchErrorHandler(response, "Failed to delete export template")
};

export const EpicRemoveExportTemplate: Epic<any, any> = EpicUtils.Create(request);
