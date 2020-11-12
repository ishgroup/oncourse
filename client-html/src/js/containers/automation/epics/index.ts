import { combineEpics } from "redux-observable";
import { EpicIntegrations } from "../containers/integrations/epics";
import { EpicExportTemplates } from "../containers/export-templates/epics";
import { EpicImportTemplates } from "../containers/import-templates/epics";
import { EpicEmailTemplates } from "../containers/email-templates/epics";

export const EpicAutomation = combineEpics(
  EpicIntegrations,
  EpicEmailTemplates,
  EpicExportTemplates,
  EpicImportTemplates
);
