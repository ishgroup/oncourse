import { _toRequestType, FULFILLED } from "../../../../../common/actions/ActionUtils";
import { Report } from "@api/model";
import { CatalogItemType } from "../../../../../model/common/Catalog";

export const GET_AUTOMATION_PDF_REPORTS_LIST = _toRequestType("get/pdf-reports/list");
export const GET_AUTOMATION_PDF_REPORTS_LIST_FULFILLED = FULFILLED(GET_AUTOMATION_PDF_REPORTS_LIST);

export const CREATE_AUTOMATION_PDF_REPORT = _toRequestType("post/pdf-reports/list");

export const UPDATE_AUTOMATION_PDF_REPORT = _toRequestType("put/pdf-reports/list");

export const UPDATE_INTERNAL_AUTOMATION_PDF_REPORT = _toRequestType("patch/pdf-reports/list");

export const REMOVE_AUTOMATION_PDF_REPORT = _toRequestType("delete/pdf-reports/list");

export const GET_AUTOMATION_PDF_REPORT = _toRequestType("get/pdf-reports/item");

export const SHOW_REPORT_FULL_SCREEN_PREVIEW = _toRequestType("get/pdf-reports/fullPreview");

export const reportFullScreenPreview = (reportId: number) => ({
  type: SHOW_REPORT_FULL_SCREEN_PREVIEW,
  payload: reportId
});

export const updateAutomationPdfReport = (report: Report) => ({
  type: UPDATE_AUTOMATION_PDF_REPORT,
  payload: { report }
});

export const updateInternalAutomationPdfReport = (report: Report) => ({
  type: UPDATE_INTERNAL_AUTOMATION_PDF_REPORT,
  payload: { report }
});

export const createAutomationPdfReport = (report: Report) => ({
  type: CREATE_AUTOMATION_PDF_REPORT,
  payload: { report }
});
export const removeAutomationPdfReport = (id: number) => ({
  type: REMOVE_AUTOMATION_PDF_REPORT,
  payload: id
});

export const getAutomationPdfReport = (id: number) => ({
  type: GET_AUTOMATION_PDF_REPORT,
  payload: id
});

export const getAutomationPdfReportsList = (selectFirst?: boolean, keyCodeToSelect?: string) => ({
  type: GET_AUTOMATION_PDF_REPORTS_LIST,
  payload: { selectFirst, keyCodeToSelect }
});

export const getAutomationPdfReportsListFulfilled = (pdfReports: CatalogItemType[]) => ({
  type: GET_AUTOMATION_PDF_REPORTS_LIST_FULFILLED,
  payload: { pdfReports }
});
