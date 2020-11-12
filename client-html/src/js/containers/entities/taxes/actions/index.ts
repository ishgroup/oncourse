import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";

export const GET_PLAIN_TAX_ITEMS = _toRequestType("get/plain/tax");
export const GET_PLAIN_TAX_ITEMS_FULFILLED = FULFILLED(GET_PLAIN_TAX_ITEMS);

export const getPlainTaxes = () => ({
  type: GET_PLAIN_TAX_ITEMS
});
