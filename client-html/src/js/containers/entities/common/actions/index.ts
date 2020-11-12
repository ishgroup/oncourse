import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";

export const GET_RECORDS_FOR_FIELD_REQUEST = _toRequestType("get/records/for/field");

export const CLEAR_QUICK_SEARCH_CONCESSION_TYPE = "clear/quickSearch/concessionType";

export const GET_QUICK_SEARCH_CONCESSION_TYPES = _toRequestType("get/quickSearch/concessionTypes");
export const GET_QUICK_SEARCH_CONCESSION_TYPES_FULFILLED = FULFILLED(GET_QUICK_SEARCH_CONCESSION_TYPES);

export const CLEAR_QUICK_SEARCH_CORPORATE_PASSES = "clear/quickSearch/corporatePasses";

export const GET_QUICK_SEARCH_CORPORATE_PASSES = _toRequestType("get/quickSearch/corporatePasses");
export const GET_QUICK_SEARCH_CORPORATE_PASSES_FULFILLED = FULFILLED(GET_QUICK_SEARCH_CORPORATE_PASSES);

export const getRecords = (entityName: string, columns: string[], form: string, field: string, search?: string) => ({
  type: GET_RECORDS_FOR_FIELD_REQUEST,
  payload: { entityName, columns, form, field, search }
});

export const clearQuickSearchConcessionTypes = (pending: boolean) => ({
  type: CLEAR_QUICK_SEARCH_CONCESSION_TYPE,
  payload: { pending }
});

export const quickSearchConcessionTypes = (search: string) => ({
  type: GET_QUICK_SEARCH_CONCESSION_TYPES,
  payload: search
});

export const clearQuickSearchCorporatePasses = (pending: boolean) => ({
  type: CLEAR_QUICK_SEARCH_CORPORATE_PASSES,
  payload: { pending }
});

export const quickSearchCorporatePasses = (search: string) => ({
  type: GET_QUICK_SEARCH_CORPORATE_PASSES,
  payload: search
});
