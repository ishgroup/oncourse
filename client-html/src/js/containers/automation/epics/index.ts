import { combineEpics } from "redux-observable";
import { EpicIntegrations } from "../containers/integrations/epics";
import { EpicExportTemplates } from "../containers/export-templates/epics";
import { EpicImportTemplates } from "../containers/import-templates/epics";
import { EpicEmailTemplates } from "../containers/email-templates/epics";
import { EpicInstallAutomation } from "./EpicInstallAutomation";
import { EpicUninstallAutomation } from "./EpicUninstallAutomation";
import { EpicExportAutomationConfig } from "./EpicExportAutomationConfig";
import { EpicImportAutomationConfig } from "./EpicImportAutomationConfig";

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
