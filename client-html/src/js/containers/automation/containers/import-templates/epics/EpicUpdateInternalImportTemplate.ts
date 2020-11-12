/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { Epic } from "redux-observable";

import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import {
  GET_IMPORT_TEMPLATE,
  GET_IMPORT_TEMPLATES_LIST,
  UPDATE_INTERNAL_IMPORT_TEMPLATE,
  UPDATE_INTERNAL_IMPORT_TEMPLATE_FULFILLED
} from "../actions/index";
import { ImportModel } from "@api/model";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import ImportTemplatesService from "../services/ImportTemplatesService";
import { FETCH_SUCCESS } from "../../../../../common/actions";
import { State } from "../../../../../reducers/state";

const request: EpicUtils.Request<State, any, { importTemplate: ImportModel }> = {
  type: UPDATE_INTERNAL_IMPORT_TEMPLATE,
  getData: ({ importTemplate }) => ImportTemplatesService.updateInternal(importTemplate),
  processData: (v, s, { importTemplate: { id } }) => {
    return [
      {
        type: UPDATE_INTERNAL_IMPORT_TEMPLATE_FULFILLED
      },
      {
        type: GET_IMPORT_TEMPLATE,
        payload: id
      },
      {
        type: GET_IMPORT_TEMPLATES_LIST
      },
      {
        type: FETCH_SUCCESS,
        payload: { message: "Import template updated" }
      }
    ];
  },
  processError: response => FetchErrorHandler(response, "Failed to update  import template")
};

export const EpicUpdateInternalImportTemplate: Epic<any, any> = EpicUtils.Create(request);
