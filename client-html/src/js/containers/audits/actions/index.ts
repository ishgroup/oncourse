import { _toRequestType, FULFILLED } from "../../../common/actions/ActionUtils";

export const GET_AUDIT_ITEM_REQUEST = _toRequestType("get/audit/item");
export const GET_AUDIT_ITEM_FULFILLED = FULFILLED(GET_AUDIT_ITEM_REQUEST);

export const RESET_SELECTED = "audit/reset/selected";

export const getAuditItem = (id: number) => ({
  type: GET_AUDIT_ITEM_REQUEST,
  payload: id
});

export const resetSelected = () => ({
  type: RESET_SELECTED
});
