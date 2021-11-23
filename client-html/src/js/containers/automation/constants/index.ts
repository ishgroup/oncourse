import * as Entities from "@aql/queryLanguageModel";
import { mapSelectItems } from "../../../common/utils/common";
import { stringLiterals } from "../../../common/utils/stringLiteral";
import { StringValueType } from "../../../model/common/CommomObjects";

export const AQL_ENTITIES = Object
  .keys(Entities)
  .filter(k => Entities[k].constructor.name !== Entities.Enum.prototype.constructor.name);

export const AQL_ENTITY_ITEMS = AQL_ENTITIES.map(mapSelectItems);

export const MESSAGE_TEMPLATE_ENTITIES = stringLiterals(
  "Application",
  "Article",
  "Contact",
  "CourseClass",
  "CourseClassTutor",
  "Enrolment",
  "Invoice",
  "Lead",
  "Membership",
  "PaymentIn",
  "PaymentOut",
  "Payslip",
  "ProductItem",
  "Voucher",
  "WaitingList"
);

export const MessageTemplateEntityItems = MESSAGE_TEMPLATE_ENTITIES.map(e => ({ label: e, value: e }));

export type MessageTemplateEntityName = StringValueType<typeof MESSAGE_TEMPLATE_ENTITIES>;
