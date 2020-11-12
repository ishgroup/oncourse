import { combineEpics } from "redux-observable";
import { EpicGetEnrolment } from "./EpicGetEnrolment";
import { EpicUpdateEnrolmentItem } from "./EpicUpdateEnrolmentItem";
import { EpicGetEnrolmentInvoiceLine } from "./EpicGetEnrolmentInvoiceLine";
import { EpicCancelEnrolment } from "./EpicCancelEnrolment";

export const EpicEnrolment = combineEpics(
  EpicGetEnrolment,
  EpicUpdateEnrolmentItem,
  EpicGetEnrolmentInvoiceLine,
  EpicCancelEnrolment
);
