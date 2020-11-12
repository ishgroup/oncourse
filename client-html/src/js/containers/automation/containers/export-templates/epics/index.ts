import { combineEpics } from "redux-observable";
import { EpicGetExportTemplatesList } from "./EpicGetExportTemplatesList";
import { EpicGetExportTemplate } from "./EpicGetExportTemplate";
import { EpicCreateExportTemplate } from "./EpicCreateExportTemplate";
import { EpicRemoveExportTemplate } from "./EpicRemoveExportTemplate";
import { EpicUpdateExportTemplate } from "./EpicUpdateExportTemplate";
import { EpicUpdateInternalExportTemplate } from "./EpicUpdateInternalExportTemplate";

export const EpicExportTemplates = combineEpics(
  EpicGetExportTemplatesList,
  EpicGetExportTemplate,
  EpicCreateExportTemplate,
  EpicRemoveExportTemplate,
  EpicUpdateExportTemplate,
  EpicUpdateInternalExportTemplate
);
