import {_toRequestType} from "../../../../common/actions/ActionUtils";
import {schema} from "normalizr";
import {IAction} from "../../../../actions/IshAction";
import Values = schema.Values;

export const OpenPayment: string = "checkout/payment/open";
export const OpenPaymentRequest: string = _toRequestType(OpenPayment);

export const MAKE_PAYMENT: string = "checkout/payment/make/payment";
export const MAKE_PAYMENT_REQUEST: string = _toRequestType(MAKE_PAYMENT);
export const UPDATE_PAYMENT_STATUS = "checkout/payment/update/payment/status";
export const UPDATE_PAYMENT_STATUS_REQUEST = _toRequestType(UPDATE_PAYMENT_STATUS);


export const makePaymentRequest = (values: Values): IAction<Values> => {
  return {
    type: MAKE_PAYMENT_REQUEST,
    payload: values
  }
};

export const getPaymentStatus = (): IAction<any> => {
  return {
    type: UPDATE_PAYMENT_STATUS_REQUEST
  }
};

export const openPayment = (): IAction<any> => {
  return {
    type: OpenPaymentRequest
  }
};
