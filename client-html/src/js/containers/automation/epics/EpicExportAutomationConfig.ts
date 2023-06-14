/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import { EXPORT_AUTOMATION_CONFIG } from "../actions";
import FetchErrorHandler from "../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { Create, Request } from "../../../common/epics/EpicUtils";
import { AutomationEntity } from "../../../model/automation/common";
import EmailTemplateService from "../containers/email-templates/services/EmailTemplateService";
import ExportTemplatesService from "../containers/export-templates/services/ExportTemplatesService";
import ImportTemplatesService from "../containers/import-templates/services/ImportTemplatesService";
import PdfService from "../containers/pdf-reports/services/PdfService";
import ScriptsService from "../containers/scripts/services/ScriptsService";
import { createAndDownloadFile } from "../../../common/utils/common";

const request: Request<string, { entity: AutomationEntity, name: string, id: number }> = {
  type: EXPORT_AUTOMATION_CONFIG,
  getData: async ({entity, id}) => {
    switch (entity) {
      case "EmailTemplate":
        return EmailTemplateService.getConfigs(id);
      case "ExportTemplate":
        return ExportTemplatesService.getConfigs(id);
      case "Import":
        return ImportTemplatesService.getConfigs(id);
      case "Report":
        return PdfService.getConfigs(id);
      case "Script":
        return ScriptsService.getConfigs(id);
    }
    return Promise.reject("Unknown automation type");
  },
  processData: (config, s, {name}) => {
    createAndDownloadFile(config, "yaml", name);
    return [];
  },
  processError: response => FetchErrorHandler(response, "Failed to export automation config")
};

export const EpicExportAutomationConfig: Epic<any, any> = Create(request);