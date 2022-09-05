import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";

export const REVERSE_PAYMENT_IN_ITEM = _toRequestType("post/paymentIn/reverse");
export const REVERSE_PAYMENT_IN_ITEM_FULFILLED = FULFILLED(REVERSE_PAYMENT_IN_ITEM);

export const GET_PAYMENT_IN_CUSTOM_VALUES = _toRequestType("get/paymentIn/customValues");
export const GET_PAYMENT_IN_CUSTOM_VALUES_FULFILLED = FULFILLED(GET_PAYMENT_IN_CUSTOM_VALUES);

export const reverse = (id: number) => ({
  type: REVERSE_PAYMENT_IN_ITEM,
  payload: id
});

export const getCustomValues = (id: number) => ({
  type: GET_PAYMENT_IN_CUSTOM_VALUES,
  payload: id
});