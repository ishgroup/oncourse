import { combineEpics } from "redux-observable";
import { EpicEmailTemplates } from "../containers/email-templates/epics";
import { EpicExportTemplates } from "../containers/export-templates/epics";
import { EpicImportTemplates } from "../containers/import-templates/epics";
import { EpicIntegrations } from "../containers/integrations/epics";
import { EpicExportAutomationConfig } from "./EpicExportAutomationConfig";
import { EpicImportAutomationConfig } from "./EpicImportAutomationConfig";
import { EpicInstallAutomation } from "./EpicInstallAutomation";
import { EpicUninstallAutomation } from "./EpicUninstallAutomation";

export const EpicAutomation = combineEpics(
  EpicExportAutomationConfig,
  EpicImportAutomationConfig,
  EpicInstallAutomation,
  EpicUninstallAutomation,
  EpicIntegrations,
  EpicEmailTemplates,
  EpicExportTemplates,
  EpicImportTemplates
);
