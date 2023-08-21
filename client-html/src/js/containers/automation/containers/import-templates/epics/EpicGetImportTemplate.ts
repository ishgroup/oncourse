/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ImportModel } from "@api/model";
import { initialize } from "redux-form";
import { Epic } from "redux-observable";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { GET_IMPORT_TEMPLATE, GET_IMPORT_TEMPLATE_FULFILLED } from "../actions";
import { IMPORT_TEMPLATES_FORM_NAME } from "../ImportTemplates";
import ImportTemplatesService from "../services/ImportTemplatesService";

const request: EpicUtils.Request<ImportModel, number> = {
  type: GET_IMPORT_TEMPLATE,
  getData: id => ImportTemplatesService.get(id),
  processData: editRecord => [
      {
        type: GET_IMPORT_TEMPLATE_FULFILLED
      },
      initialize(IMPORT_TEMPLATES_FORM_NAME, editRecord)
    ],
  processError: response => [
      ...FetchErrorHandler(response, "Failed to get import template"),
      initialize(IMPORT_TEMPLATES_FORM_NAME, null)
    ]
};

export const EpicGetImportTemplate: Epic<any, any> = EpicUtils.Create(request);
