/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import { IMPORT_AUTOMATION_CONFIG } from "../actions";
import FetchErrorHandler from "../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { Create, Request } from "../../../common/epics/EpicUtils";
import { AutomationEntity } from "../../../model/automation/common";
import EmailTemplateService from "../containers/email-templates/services/EmailTemplateService";
import ExportTemplatesService from "../containers/export-templates/services/ExportTemplatesService";
import ImportTemplatesService from "../containers/import-templates/services/ImportTemplatesService";
import PdfService from "../containers/pdf-reports/services/PdfService";
import ScriptsService from "../containers/scripts/services/ScriptsService";
import { showMessage } from "../../../common/actions";
import { getEmailTemplate } from "../containers/email-templates/actions";
import { getExportTemplate } from "../containers/export-templates/actions";
import { getImportTemplate } from "../containers/import-templates/actions";
import { getAutomationPdfReport } from "../containers/pdf-reports/actions";
import { getScriptItem } from "../containers/scripts/actions";
import { AutomationConfigs } from "@api/model";

const request: Request<any, { entity: AutomationEntity, id: number, config: AutomationConfigs }> = {
  type: IMPORT_AUTOMATION_CONFIG,
  getData: async ({entity, id, config}) => {
    switch (entity) {
      case "EmailTemplate":
        return EmailTemplateService.updateConfigs(id, config).then(() => getEmailTemplate(id));
      case "ExportTemplate":
        return ExportTemplatesService.updateConfigs(id, config).then(() => getExportTemplate(id));
      case "Import":
        return ImportTemplatesService.updateConfigs(id, config).then(() => getImportTemplate(id));
      case "Report":
        return PdfService.updateConfigs(id, config).then(() => getAutomationPdfReport(id));
      case "Script":
        return ScriptsService.updateConfigs(id, config).then(() => getScriptItem(id));
    }
    return Promise.reject("Unknown automation type");
  },
  processData: action => [
    action,
    showMessage({message: "Automation config updated", success: true})
  ],
  processError: response => FetchErrorHandler(response, "Failed to update automation config")
};

export const EpicImportAutomationConfig: Epic<any, any> = Create(request);