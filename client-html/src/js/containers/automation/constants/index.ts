import * as Entities from "@aql/queryLanguageModel";
import { mapSelectItems } from "../../../common/utils/common";
import { stringLiterals } from "../../../common/utils/stringLiteral";
import { StringValueType } from "../../../model/common/CommomObjects";

export const AQL_ENTITIES = Object
  .keys(Entities)
  .filter(k => Entities[k].constructor.name !== Entities.Enum.prototype.constructor.name);

export const AQL_ENTITY_ITEMS = AQL_ENTITIES.map(mapSelectItems);

export const ENTITIES = stringLiterals(
  "Account",
  "AccountTransaction",
  "Application",
  "Article",
  "ArticleProduct",
  "Assessment",
  "Audit",
  "Banking",
  "Certificate",
  "ClassCost",
  "Contact",
  "ContactDataRowDelegator",
  "CorporatePass",
  "Course",
  "CourseClass",
  "CourseClassTutor",
  "Document",
  "Discount",
  "DiscountCourseClass",
  "Enrolment",
  "Invoice",
  "InvoiceLine",
  "Membership",
  "MembershipProduct",
  "Message",
  "Module",
  "Outcome",
  "PayLine",
  "PaymentIn",
  "PaymentInterface",
  "PaymentOut",
  "Payslip",
  "ProductItem",
  "PriorLearning",
  "Qualification",
  "Room",
  "Script",
  "Session",
  "Site",
  "Student",
  "Survey",
  "TrainingPackage",
  "Tutor",
  "TutorAttendance",
  "Voucher",
  "VoucherProduct",
  "WaitingList"
);

export const MESSAGE_TEMPLATE_ENTITIES = stringLiterals(
  "Application",
  "Article",
  "Contact",
  "CourseClass",
  "CourseClassTutor",
  "Enrolment",
  "Invoice",
  "Membership",
  "PaymentIn",
  "PaymentOut",
  "Payslip",
  "ProductItem",
  "Voucher",
  "WaitingList"
);

export const EntityItems = ENTITIES.map(e => ({ label: e, value: e }));

export const MessageTemplateEntityItems = MESSAGE_TEMPLATE_ENTITIES.map(e => ({ label: e, value: e }));

export type EntityName = StringValueType<typeof ENTITIES>;

export type MessageTemplateEntityName = StringValueType<typeof MESSAGE_TEMPLATE_ENTITIES>;
