import {_toRequestType} from "../../../../common/actions/ActionUtils";
import {schema} from "normalizr";
import Values = schema.Values;

export const OpenPayment: string = "checkout/payment/open";
export const OpenPaymentRequest: string = _toRequestType(OpenPayment);

export const MAKE_PAYMENT: string = "checkout/payment/make/payment";
export const MAKE_PAYMENT_REQUEST: string = _toRequestType(MAKE_PAYMENT);


export const makePaymentRequest = (values: Values): { type: string, payload: Values } => {
  return {
    type: MAKE_PAYMENT_REQUEST,
    payload: values
  }
};

export const openPayment = (): { type: string } => {
  return {
    type: OpenPaymentRequest
  }
};
