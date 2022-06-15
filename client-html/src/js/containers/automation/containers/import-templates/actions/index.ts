import { ExecuteImportRequest } from "@api/model";
import { _toRequestType, FULFILLED } from "../../../../../common/actions/ActionUtils";
import { CatalogItemType } from "../../../../../model/common/Catalog";

export const GET_IMPORT_TEMPLATES_LIST = _toRequestType("get/import-templates/list");
export const GET_IMPORT_TEMPLATES_LIST_FULFILLED = FULFILLED(GET_IMPORT_TEMPLATES_LIST);

export const CREATE_IMPORT_TEMPLATE = _toRequestType("post/import-templates/list");

export const UPDATE_IMPORT_TEMPLATE = _toRequestType("put/import-templates/list");
export const UPDATE_IMPORT_TEMPLATE_FULFILLED = FULFILLED(UPDATE_IMPORT_TEMPLATE);

export const UPDATE_INTERNAL_IMPORT_TEMPLATE = _toRequestType("patch/import-templates/list");
export const UPDATE_INTERNAL_IMPORT_TEMPLATE_FULFILLED = FULFILLED(UPDATE_INTERNAL_IMPORT_TEMPLATE);

export const REMOVE_IMPORT_TEMPLATE = _toRequestType("delete/import-templates/list");
export const REMOVE_IMPORT_TEMPLATE_FULFILLED = FULFILLED(REMOVE_IMPORT_TEMPLATE);

export const GET_IMPORT_TEMPLATE = _toRequestType("get/import-templates/item");
export const GET_IMPORT_TEMPLATE_FULFILLED = FULFILLED(GET_IMPORT_TEMPLATE);

export const POST_IMPORT_RUN_REQUEST = _toRequestType("post/importItem/execute");
export const POST_IMPORT_RUN_REQUEST_FULFILLED = FULFILLED(POST_IMPORT_RUN_REQUEST);

export const updateImportTemplate = (importTemplate: any) => ({
    type: UPDATE_IMPORT_TEMPLATE,
    payload: { importTemplate }
});

export const updateInternalImportTemplate = (importTemplate: any) => ({
    type: UPDATE_INTERNAL_IMPORT_TEMPLATE,
    payload: { importTemplate }
});

export const createImportTemplate = (importTemplate: any) => ({
    type: CREATE_IMPORT_TEMPLATE,
    payload: { importTemplate }
});

export const removeImportTemplate = (id: number) => ({
    type: REMOVE_IMPORT_TEMPLATE,
    payload: id
});

export const getImportTemplate = (id: number) => ({
    type: GET_IMPORT_TEMPLATE,
    payload: id
});

export const getImportTemplatesList = (selectFirst?: boolean, keyCodeToSelect?: string) => ({
    type: GET_IMPORT_TEMPLATES_LIST,
    payload: { selectFirst, keyCodeToSelect }
});

export const runImport = (executeImportRequest: ExecuteImportRequest, files: any[]) => ({
    type: POST_IMPORT_RUN_REQUEST,
    payload: { executeImportRequest, files }
});

export const getImportTemplatesListFulfilled = (importTemplates: CatalogItemType[]) => ({
    type: GET_IMPORT_TEMPLATES_LIST_FULFILLED,
    payload: { importTemplates }
});
