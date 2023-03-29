/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

import { ExportRequest, OutputType, PrintRequest } from "@api/model";
import { _toRequestType, FULFILLED } from "../../../../../actions/ActionUtils";

export const GET_OVERLAY_ITEMS = _toRequestType("get/overlay");
export const GET_OVERLAY_ITEMS_FULFILLED = FULFILLED(GET_OVERLAY_ITEMS);

export const ADD_PRINT_OVERLAY = _toRequestType("post/overlay");
export const ADD_PRINT_OVERLAY_FULFILLED = FULFILLED(ADD_PRINT_OVERLAY);

export const DO_PRINT_REQUEST = _toRequestType("put/list/share/print");
export const DO_PRINT_REQUEST_FULFILLED = FULFILLED(DO_PRINT_REQUEST);

export const GET_PRINT_RESULT = _toRequestType("get/list/share/print");

export const GET_PDF_REPORTS = _toRequestType("get/list/share");
export const GET_PDF_REPORTS_FULFILLED = FULFILLED(GET_PDF_REPORTS);

export const GET_EXPORT_TEMPLATES = _toRequestType("get/list/share/csv/template");
export const GET_EXPORT_TEMPLATES_FULFILLED = FULFILLED(GET_EXPORT_TEMPLATES);

export const GET_EXPORT_RESULT = _toRequestType("get/list/export");

export const POST_EXPORT_REQUEST = _toRequestType("post/list/export");

export const SET_PRINT_VALIDATING_STATUS = "set/print/validation";

export const DELETE_PDF_REPORT_PREVIEW = _toRequestType("delete/list/share/report/preview");

export const DELETE_EXPORT_TEMPLATE_PREVIEW = _toRequestType("delete/list/share/csv/template/preview");

export const deletePdfReportPreview = (id: number) => ({
  type: DELETE_PDF_REPORT_PREVIEW,
  payload: id
});

export const deleteExportTemplatePreview = (id: number) => ({
  type: DELETE_EXPORT_TEMPLATE_PREVIEW,
  payload: id
});

export const setPrintValidatingStatus = (validating: boolean) => ({
  type: SET_PRINT_VALIDATING_STATUS,
  payload: { validating }
});

export const getOverlayItems = (overlayToSelect?: string) => ({
  type: GET_OVERLAY_ITEMS,
  payload: { overlayToSelect }
});

export const addPrintOverlay = (fileName: string, overlay: File) => ({
  type: ADD_PRINT_OVERLAY,
  payload: { fileName, overlay }
});

export const doPrintRequest = (rootEntity: string, printRequest: PrintRequest) => ({
  type: DO_PRINT_REQUEST,
  payload: { rootEntity, printRequest }
});

export const getPrintResult = (processId: string) => ({
  type: GET_PRINT_RESULT,
  payload: processId
});

export const getShareList = (entityName: string) => ({
  type: GET_PDF_REPORTS,
  payload: entityName
});

export const getExportTemplates = (entityName: string) => ({
  type: GET_EXPORT_TEMPLATES,
  payload: entityName
});

export const getExportResult = (entityName: string, processId: string, outputType: OutputType, isClipboard?: boolean) => ({
  type: GET_EXPORT_RESULT,
  payload: {
    entityName, processId, outputType, isClipboard
  }
});

export const runExport = (exportRequest: ExportRequest, outputType: OutputType, isClipboard: boolean) => ({
  type: POST_EXPORT_REQUEST,
  payload: { exportRequest, outputType, isClipboard }
});
