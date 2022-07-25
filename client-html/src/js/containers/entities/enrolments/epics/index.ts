import { combineEpics } from "redux-observable";
import { EpicUpdateEnrolmentItem } from "./EpicUpdateEnrolmentItem";
import { EpicGetEnrolmentInvoiceLine } from "./EpicGetEnrolmentInvoiceLine";
import { EpicCancelEnrolment } from "./EpicCancelEnrolment";

export const EpicEnrolment = combineEpics(
  EpicUpdateEnrolmentItem,
  EpicGetEnrolmentInvoiceLine,
  EpicCancelEnrolment
);