import { combineEpics } from "redux-observable";
import { EpicCancelEnrolment } from "./EpicCancelEnrolment";
import { EpicGetEnrolmentInvoiceLine } from "./EpicGetEnrolmentInvoiceLine";
import { EpicProcessOutcomeRelatedFields } from "./EpicProcessOutcomeRelatedFields";

export const EpicEnrolment = combineEpics(
  EpicProcessOutcomeRelatedFields,
  EpicGetEnrolmentInvoiceLine,
  EpicCancelEnrolment
);