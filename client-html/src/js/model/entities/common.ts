import { stringLiterals } from "../../common/utils/stringLiteral";
import { StringValueType } from "../common/CommomObjects";

export interface SelectItemDefault {
  value?: any;
  label?: string;
}

export interface FindEntityAql {
  AQL: string;
  id : string;
  action: string;
}

export interface FindEntityState {
  data?: FindEntityAql[];
}

export const ENTITIES = stringLiterals(
  "Account",
  "AbstractInvoice",
  "AccountTransaction",
  "Application",
  "Article",
  "ArticleProduct",
  "Assessment",
  "AssessmentSubmission",
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
  "Lead",
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

export const EntityItems = ENTITIES.map(e => ({ label: e, value: e }));

export type EntityName = StringValueType<typeof ENTITIES>;
