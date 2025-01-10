import { CancelEnrolment } from "@api/model";
import { _toRequestType } from "../../../../common/actions/ActionUtils";
import { EnrolmentDialogs, OutcomeChangeField } from "../../../../model/entities/Enrolment";

export const GET_ENROLMENT_INVOICE_LINES = _toRequestType("get/enrolment/invoiceLines");
export const SET_ENROLMENT_INVOICE_LINES = "set/enrolment/invoiceLines";

export const CANCEL_ENROLMENT = "cancel/enrolment";

export const SET_ENROLMENTS_DIALOG = "set/enrolment/dialog";

export const SET_ENROLMENTS_PROCESSING = "set/enrolment/processing";

export const SET_OTCOME_CHANGE_FIELDS =  "set/enrolment/outcomeChangeFields";

export const PROCESS_OTCOME_CHANGE_FIELDS = _toRequestType("process/enrolment/outcomeChangeFields");

export const setOtcomeChangeFields = (changedOutcomeFields: OutcomeChangeField[]) => ({
  type: SET_OTCOME_CHANGE_FIELDS,
  payload: { changedOutcomeFields }
});

export const setEnrolmentsDialog = (dialogOpened: EnrolmentDialogs) => ({
  type: SET_ENROLMENTS_DIALOG,
  payload: { dialogOpened }
});

export const setEnrolmentsProcessing = (processing: boolean) => ({
  type: SET_ENROLMENTS_PROCESSING,
  payload: { processing }
});

export const processOtcomeChangeFields = (ids: number[]) => ({
  type: PROCESS_OTCOME_CHANGE_FIELDS,
  payload: ids
});

export const getEnrolmentInvoiceLines = (id: string) => ({
  type: GET_ENROLMENT_INVOICE_LINES,
  payload: id
});

export const cancelEnrolment = (values: CancelEnrolment, type: string) => ({
  type: CANCEL_ENROLMENT,
  payload: { values, type }
});