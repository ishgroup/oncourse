import { combineEpics } from "redux-observable";
import { EpicGetImportTemplatesList } from "./EpicGetImportTemplatesList";
import { EpicGetImportTemplate } from "./EpicGetImportTemplate";
import { EpicCreateImportTemplate } from "./EpicCreateImportTemplate";
import { EpicRemoveImportTemplate } from "./EpicRemoveImportTemplate";
import { EpicRunImportItem } from "./EpicRunImportItem";
import { EpicUpdateImportTemplate } from "./EpicUpdateImportTemplate";
import { EpicUpdateInternalImportTemplate } from "./EpicUpdateInternalImportTemplate";

export const EpicImportTemplates = combineEpics(
  EpicGetImportTemplatesList,
  EpicGetImportTemplate,
  EpicCreateImportTemplate,
  EpicRemoveImportTemplate,
  EpicUpdateImportTemplate,
  EpicUpdateInternalImportTemplate,
  EpicRunImportItem
);
