import { combineEpics } from "redux-observable";
import { EpicGetEnrolmentInvoiceLine } from "./EpicGetEnrolmentInvoiceLine";
import { EpicCancelEnrolment } from "./EpicCancelEnrolment";

export const EpicEnrolment = combineEpics(
  EpicGetEnrolmentInvoiceLine,
  EpicCancelEnrolment
);