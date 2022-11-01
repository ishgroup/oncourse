import { _toRequestType, FULFILLED } from "../../../../../common/actions/ActionUtils";
import { ExportTemplate } from "@api/model";
import { CatalogItemType } from "../../../../../model/common/Catalog";

export const GET_EXPORT_TEMPLATES_LIST = _toRequestType("get/export-templates/list");
export const GET_EXPORT_TEMPLATES_LIST_FULFILLED = FULFILLED(GET_EXPORT_TEMPLATES_LIST);

export const CREATE_EXPORT_TEMPLATE = _toRequestType("post/export-templates/list");

export const UPDATE_EXPORT_TEMPLATE = _toRequestType("put/export-templates/list");

export const UPDATE_INTERNAL_EXPORT_TEMPLATE = _toRequestType("patch/export-templates/list");

export const REMOVE_EXPORT_TEMPLATE = _toRequestType("delete/export-templates/list");

export const GET_EXPORT_TEMPLATE = _toRequestType("get/export-templates/item");

export const updateExportTemplate = (exportTemplate: ExportTemplate) => ({
  type: UPDATE_EXPORT_TEMPLATE,
  payload: { exportTemplate }
});

export const updateInternalExportTemplate = (exportTemplate: ExportTemplate) => ({
  type: UPDATE_INTERNAL_EXPORT_TEMPLATE,
  payload: { exportTemplate }
});

export const createExportTemplate = (exportTemplate: ExportTemplate) => ({
  type: CREATE_EXPORT_TEMPLATE,
  payload: { exportTemplate }
});
export const removeExportTemplate = (id: number) => ({
  type: REMOVE_EXPORT_TEMPLATE,
  payload: id
});

export const getExportTemplate = (id: number) => ({
  type: GET_EXPORT_TEMPLATE,
  payload: id
});

export const getExportTemplatesList = (selectFirst?: boolean, keyCodeToSelect?: string) => ({
  type: GET_EXPORT_TEMPLATES_LIST,
  payload: { selectFirst, keyCodeToSelect }
});


export const getExportTemplatesListFulfilled = (exportTemplates: CatalogItemType[]) => ({
  type: GET_EXPORT_TEMPLATES_LIST_FULFILLED,
  payload: { exportTemplates }
});
