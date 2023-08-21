import { combineEpics } from "redux-observable";
import { EpicCreateExportTemplate } from "./EpicCreateExportTemplate";
import { EpicGetExportTemplate } from "./EpicGetExportTemplate";
import { EpicGetExportTemplatesList } from "./EpicGetExportTemplatesList";
import { EpicGetFullScreenPreview } from "./EpicGetFullScreenPreview";
import { EpicRemoveExportTemplate } from "./EpicRemoveExportTemplate";
import { EpicUpdateExportTemplate } from "./EpicUpdateExportTemplate";
import { EpicUpdateInternalExportTemplate } from "./EpicUpdateInternalExportTemplate";

export const EpicExportTemplates = combineEpics(
  EpicGetExportTemplatesList,
  EpicGetExportTemplate,
  EpicCreateExportTemplate,
  EpicRemoveExportTemplate,
  EpicUpdateExportTemplate,
  EpicUpdateInternalExportTemplate,
  EpicGetFullScreenPreview,
);
