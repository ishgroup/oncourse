/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import { ImportModel } from "@api/model";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { CREATE_IMPORT_TEMPLATE, CREATE_IMPORT_TEMPLATE_FULFILLED, GET_IMPORT_TEMPLATES_LIST } from "../actions";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import ImportTemplatesService from "../services/ImportTemplatesService";
import { FETCH_SUCCESS } from "../../../../../common/actions";

const request: EpicUtils.Request<{ importTemplate: ImportModel }, { importTemplate: ImportModel }> = {
  type: CREATE_IMPORT_TEMPLATE,
  getData: ({ importTemplate }) => ImportTemplatesService.create(importTemplate),
  processData: (v, s, { importTemplate }) => [
      {
        type: CREATE_IMPORT_TEMPLATE_FULFILLED
      },
      {
        type: GET_IMPORT_TEMPLATES_LIST,
        payload: { keyCodeToSelect: importTemplate.keyCode }
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Import template created" }
      }
    ],
  processError: response => FetchErrorHandler(response, "Failed to create import template")
};

export const EpicCreateImportTemplate: Epic<any, any> = EpicUtils.Create(request);
