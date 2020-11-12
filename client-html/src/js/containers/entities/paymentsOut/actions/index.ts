import { _toRequestType, FULFILLED } from "../../../../common/actions/ActionUtils";
import { PaymentOut } from "@api/model";

export const GET_PAYMENT_OUT_ITEM = _toRequestType("get/paymentOut");
export const GET_PAYMENT_OUT_ITEM_FULFILLED = FULFILLED(GET_PAYMENT_OUT_ITEM);

export const POST_PAYMENT_OUT_ITEM = _toRequestType("post/paymentOut");
export const POST_PAYMENT_OUT_ITEM_FULFILLED = FULFILLED(POST_PAYMENT_OUT_ITEM);

export const UPDATE_PAYMENT_OUT_ITEM = _toRequestType("put/paymentOut");
export const UPDATE_PAYMENT_OUT_ITEM_FULFILLED = FULFILLED(UPDATE_PAYMENT_OUT_ITEM);

export const GET_ADD_PAYMENT_OUT_CONTACT = _toRequestType("get/paymentOut/contact");
export const GET_ADD_PAYMENT_OUT_CONTACT_FULFILLED = FULFILLED(GET_ADD_PAYMENT_OUT_CONTACT);

export const GET_ACTIVE_PAYMENT_OUT_METHODS = _toRequestType("get/paymentOut/methods");
export const GET_ACTIVE_PAYMENT_OUT_METHODS_FULFILLED = FULFILLED(GET_ACTIVE_PAYMENT_OUT_METHODS);

export const GET_REFUNDABLE_PAYMENTS = _toRequestType("get/paymentOut/refundablePayments");
export const GET_REFUNDABLE_PAYMENTS_FULFILLED = FULFILLED(GET_REFUNDABLE_PAYMENTS);

export const GET_ADD_PAYMENT_OUT_VALUES = _toRequestType("get/paymentOut/customValues");
export const GET_ADD_PAYMENT_OUT_VALUES_FULFILLED = FULFILLED(GET_ADD_PAYMENT_OUT_VALUES);

export const GET_PAYMENT_OUT_CUSTOM_VALUES = _toRequestType("get/paymentOut/customValues");
export const GET_PAYMENT_OUT_CUSTOM_VALUES_FULFILLED = FULFILLED(GET_PAYMENT_OUT_CUSTOM_VALUES);

export const getPaymentOut = (id: string) => ({
  type: GET_PAYMENT_OUT_ITEM,
  payload: id
});

export const postPaymentOut = (paymentOut: PaymentOut) => ({
  type: POST_PAYMENT_OUT_ITEM,
  payload: { paymentOut }
});

export const updatePaymentOut = (id: string, paymentOut: PaymentOut) => ({
  type: UPDATE_PAYMENT_OUT_ITEM,
  payload: { id, paymentOut }
});

export const getAddPaymentOutContact = (id: number) => ({
  type: GET_ADD_PAYMENT_OUT_CONTACT,
  payload: id
});

export const getActivePaymentOutMethods = () => ({
  type: GET_ACTIVE_PAYMENT_OUT_METHODS
});

export const getCustomValues = (id: number, paymentOut: PaymentOut) => ({
  type: GET_PAYMENT_OUT_CUSTOM_VALUES,
  payload: { id, paymentOut }
});
