import {_toRequestType} from "../../../../common/actions/ActionUtils";
import {IAction} from "../../../../actions/IshAction";
import {PaymentResponse} from "../../../../model/checkout/payment/PaymentResponse";
import {Values} from "../services/PaymentService";

export const OpenPayment: string = "checkout/payment/open";
export const OpenPaymentRequest: string = _toRequestType(OpenPayment);

export const MAKE_PAYMENT: string = "checkout/payment/make/payment";
export const PROCESS_PAYMENT: string = "checkout/payment/process/payment";


export const UPDATE_PAYMENT_STATUS = "checkout/payment/update/payment/status";

export const GET_PAYMENT_STATUS = "checkout/payment/get/payment/status";

export const RESET_PAYMENT_STATE = "checkout/payment/reset/payment/state";

export const GET_CORPORATE_PASS = "checkout/payment/get/corporatePass";

export const makePayment = (values: Values): IAction<Values> => {
  return {
    type: MAKE_PAYMENT,
    payload: values,
  };
};

export const processPayment = (values: Values): IAction<Values> => {
  return {
    type: PROCESS_PAYMENT,
    payload: values,
  };
};


export const getPaymentStatus = (): IAction<any> => {
  return {
    type: GET_PAYMENT_STATUS,
  };
};

export const updatePaymentStatus = (response: PaymentResponse): IAction<PaymentResponse> => {
  return {
    type: UPDATE_PAYMENT_STATUS,
    payload: response,
  };
};

export const resetPaymentState = () => {
  return {
    type: RESET_PAYMENT_STATE,
  };
};

export const getCorporatePass = (code: string) => ({
  type: GET_CORPORATE_PASS,
  payload: code,
});
