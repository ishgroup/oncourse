import { combineEpics } from "redux-observable";
import { EpicCreateEmailTemplate } from "./EpicCreateEmailTemplate";
import { EpicGetEmailTemplatesList } from "./EpicGetEmailTemplatesList";
import { EpicGetEmailTemplate } from "./EpicGetEmailTemplate";
import { EpicRemoveEmailTemplate } from "./EpicRemoveEmailTemplate";
import { EpicUpdateEmailTemplate } from "./EpicUpdateEmailTemplate";
import { EpicUpdateInternalEmailTemplate } from "./EpicUpdateInternalEmailTemplate";

export const EpicEmailTemplates = combineEpics(
  EpicGetEmailTemplatesList,
  EpicGetEmailTemplate,
  EpicCreateEmailTemplate,
  EpicRemoveEmailTemplate,
  EpicUpdateEmailTemplate,
  EpicUpdateInternalEmailTemplate
);
