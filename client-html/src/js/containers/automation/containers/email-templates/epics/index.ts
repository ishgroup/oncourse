import { combineEpics } from "redux-observable";
import { EpicCreateEmailTemplate } from "./EpicCreateEmailTemplate";
import { EpicGetEmailTemplate } from "./EpicGetEmailTemplate";
import { EpicGetEmailTemplatesList } from "./EpicGetEmailTemplatesList";
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
