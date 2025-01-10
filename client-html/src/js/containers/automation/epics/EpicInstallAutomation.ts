/*
 * Copyright ish group pty ltd 2022.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

import { Epic } from "redux-observable";
import { showMessage } from "../../../common/actions";
import FetchErrorHandler from "../../../common/api/fetch-errors-handlers/FetchErrorHandler";
import { Create, Request } from "../../../common/epics/EpicUtils";
import { AutomationEntity } from "../../../model/automation/common";
import { CatalogItemType } from "../../../model/common/Catalog";
import { INSTALL_AUTOMATION } from "../actions";
import EmailTemplateService from "../containers/email-templates/services/EmailTemplateService";
import ExportTemplatesService from "../containers/export-templates/services/ExportTemplatesService";
import ImportTemplatesService from "../containers/import-templates/services/ImportTemplatesService";
import PdfService from "../containers/pdf-reports/services/PdfService";
import ScriptsService from "../containers/scripts/services/ScriptsService";

const request: Request<any, { automation: CatalogItemType, entity: AutomationEntity }> = {
  type: INSTALL_AUTOMATION,
  getData: async ({ automation: { id, keyCode }, entity }) => {
    const isInternal = keyCode?.startsWith("ish.");
    switch (entity) {
      case "EmailTemplate": {
         const template = await EmailTemplateService.get(id);
         const updated = { ...template, status: "Enabled" as any };
         return isInternal ? EmailTemplateService.updateInternal(updated) : EmailTemplateService.update(id, updated);
      }
      case "ExportTemplate": {
        const template = await ExportTemplatesService.get(id);
        const updated = { ...template, status: "Enabled" as any };
        return isInternal ? ExportTemplatesService.updateInternal(updated) : ExportTemplatesService.update(id, updated);
      }
      case "Import": {
        const template = await ImportTemplatesService.get(id);
        const updated = { ...template, status: "Enabled" as any };
        return isInternal ? ImportTemplatesService.updateInternal(updated) : ImportTemplatesService.update(id, updated);
      }
      case "Report": {
        const report = await PdfService.getReport(id);
        const updated = { ...report, status: "Enabled" as any };
        return isInternal ? PdfService.updateInternalReport(updated) : PdfService.updateReport(id, updated);
      }
      case "Script": {
        const script = await ScriptsService.getScriptItem(id);
        const updated = { ...script, status: "Enabled" as any };
        return isInternal ? ScriptsService.patchScriptItem(id, updated) : ScriptsService.saveScriptItem(id, updated);
      }
    }
    return Promise.reject("Unknown automation type");
  },
  processData: () => [showMessage({ message: "Automation installed", success: true })],
  processError: response => FetchErrorHandler(response, "Failed to install automation")
};

export const EpicInstallAutomation: Epic<any, any> = Create(request);
