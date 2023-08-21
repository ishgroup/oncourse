import { combineEpics } from "redux-observable";
import { EpicCancelEnrolment } from "./EpicCancelEnrolment";
import { EpicGetEnrolmentInvoiceLine } from "./EpicGetEnrolmentInvoiceLine";

export const EpicEnrolment = combineEpics(
  EpicGetEnrolmentInvoiceLine,
  EpicCancelEnrolment
);