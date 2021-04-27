/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { GET_IMPORT_TEMPLATES_LIST, REMOVE_IMPORT_TEMPLATE, REMOVE_IMPORT_TEMPLATE_FULFILLED } from "../actions/index";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import ImportTemplatesService from "../services/ImportTemplatesService";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import history from "../../../../../constants/History";

const request: EpicUtils.Request<any, number> = {
  type: REMOVE_IMPORT_TEMPLATE,
  getData: id => ImportTemplatesService.remove(id),
  processData: () => {
    history.push("/automation/import-templates");

    return [
      {
        type: REMOVE_IMPORT_TEMPLATE_FULFILLED
      },
      {
        type: GET_IMPORT_TEMPLATES_LIST
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Import template deleted" }
      }
    ];
  },
  processError: response => FetchErrorHandler(response, "Failed to delete import template")
};

export const EpicRemoveImportTemplate: Epic<any, any> = EpicUtils.Create(request);
