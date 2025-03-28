import * as Entities from "@aql/queryLanguageModel";
import { mapSelectItems, StringValueType } from "ish-ui";
import { stringLiterals } from "../../../common/utils/stringLiteral";

export const AQL_ENTITIES = Object
  .keys(Entities)
  .filter(k => Entities[k].constructor.name !== Entities.Enum.prototype.constructor.name);

export const AQL_ENTITY_ITEMS = AQL_ENTITIES.map(mapSelectItems);

export const MESSAGE_TEMPLATE_ENTITIES = stringLiterals(
    "Application",
    "Article",
    "Checkout",
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
    "Quote",
    "Voucher",
    "WaitingList"
);

export const MessageTemplateEntityItems = MESSAGE_TEMPLATE_ENTITIES.map(e => ({ label: e, value: e }));

export type MessageTemplateEntityName = StringValueType<typeof MESSAGE_TEMPLATE_ENTITIES>;
