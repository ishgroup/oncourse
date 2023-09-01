import { CancelEnrolment } from "@api/model";
import { _toRequestType } from "../../../../common/actions/ActionUtils";
import { EnrolmentDialogs } from "../../../../model/entities/Enrolment";

export const GET_ENROLMENT_INVOICE_LINES = _toRequestType("get/enrolment/invoiceLines");
export const SET_ENROLMENT_INVOICE_LINES = "set/enrolment/invoiceLines";

export const CANCEL_ENROLMENT = _toRequestType("cancel/enrolment");

export const SET_ENROLMENTS_DIALOG = _toRequestType("set/enrolment/dialog");

export const SET_ENROLMENTS_PROCESSING = _toRequestType("set/enrolment/processing");

export const setEnrolmentsDialog = (dialogOpened: EnrolmentDialogs) => ({
  type: SET_ENROLMENTS_DIALOG,
  payload: { dialogOpened }
});

export const setEnrolmentsProcessing = (processing: boolean) => ({
  type: SET_ENROLMENTS_PROCESSING,
  payload: { processing }
});

export const getEnrolmentInvoiceLines = (id: string) => ({
  type: GET_ENROLMENT_INVOICE_LINES,
  payload: id
});

export const cancelEnrolment = (values: CancelEnrolment, type: string) => ({
  type: CANCEL_ENROLMENT,
  payload: { values, type }
});