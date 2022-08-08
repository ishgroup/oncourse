import { CancelEnrolment, Enrolment } from "@api/model";
import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";
import { EnrolmentDialogs } from "../../../../model/entities/Enrolment";

export const GET_ENROLMENT_ITEM = _toRequestType("get/enrolment");
export const GET_ENROLMENT_ITEM_FULFILLED = FULFILLED(GET_ENROLMENT_ITEM);

export const UPDATE_ENROLMENT_ITEM = _toRequestType("put/enrolment");
export const UPDATE_ENROLMENT_ITEM_FULFILLED = FULFILLED(UPDATE_ENROLMENT_ITEM);

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

export const getEnrolment = (id: string) => ({
  type: GET_ENROLMENT_ITEM,
  payload: id
});

export const updateEnrolment = (id: number, enrolment: Enrolment) => ({
  type: UPDATE_ENROLMENT_ITEM,
  payload: { id, enrolment }
});

export const getEnrolmentInvoiceLines = (id: string) => ({
  type: GET_ENROLMENT_INVOICE_LINES,
  payload: id
});

export const cancelEnrolment = (values: CancelEnrolment, type: string) => ({
  type: CANCEL_ENROLMENT,
  payload: { values, type }
});
