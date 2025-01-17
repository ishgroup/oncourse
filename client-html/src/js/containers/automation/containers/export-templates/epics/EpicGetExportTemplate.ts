/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ExportTemplate } from "@api/model";
import { initialize } from "redux-form";
import { Epic } from "redux-observable";
import FetchErrorHandler from "../../../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import * as EpicUtils from "../../../../../common/epics/EpicUtils";
import { GET_EXPORT_TEMPLATE } from "../actions";
import { EXPORT_TEMPLATES_FORM_NAME } from "../ExportTemplates";
import ExportTemplatesService from "../services/ExportTemplatesService";

const request: EpicUtils.Request<ExportTemplate, number> = {
  type: GET_EXPORT_TEMPLATE,
  getData: id => ExportTemplatesService.get(id),
  processData: editRecord => [
      initialize(EXPORT_TEMPLATES_FORM_NAME, editRecord)
    ],
  processError: response => [
      ...FetchErrorHandler(response, "Failed to get export template"),
      initialize(EXPORT_TEMPLATES_FORM_NAME, null)
    ]
};

export const EpicGetExportTemplate: Epic<any, any> = EpicUtils.Create(request);
