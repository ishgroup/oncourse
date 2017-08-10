import {_toRequestType} from "../../../../common/actions/ActionUtils";
import {IAction} from "../../../../actions/IshAction";
import {PaymentResponse} from "../../../../model";
import {CreditCardFormValues, CorporatePassFormValues} from "../services/PaymentService";

export const OpenPayment: string = "checkout/payment/open";
export const OpenPaymentRequest: string = _toRequestType(OpenPayment);

export const SUBMIT_PAYMENT_CREDIT_CARD: string = "checkout/payment/submit/creditCard";
export const SUBMIT_PAYMENT_CORPORATE_PASS: string = "checkout/payment/submit/corporatePass";
export const PROCESS_PAYMENT: string = "checkout/payment/process/payment";


export const UPDATE_PAYMENT_STATUS = "checkout/payment/update/payment/status";

export const GET_PAYMENT_STATUS = "checkout/payment/get/payment/status";

export const RESET_PAYMENT_STATE = "checkout/payment/reset/payment/state";
export const RESET_PAYMENT_STATE_ON_DESTROY: string = "checkout/payment/reset/payment/onDestroy";


export const GET_CORPORATE_PASS_REQUEST = "checkout/payment/get/corporatePass";
export const APPLY_CORPORATE_PASS = "checkout/payment/apply/corporatePass";
export const RESET_CORPORATE_PASS = "checkout/payment/reset/corporatePass";

export const CHANGE_TAB = "checkout/payment/change/tab";

export const submitPaymentCreditCard = (values: CreditCardFormValues): IAction<CreditCardFormValues> => {
  return {
    type: SUBMIT_PAYMENT_CREDIT_CARD,
    payload: values,
  };
};

export const submitPaymentCorporatePass = (values: CorporatePassFormValues): IAction<CorporatePassFormValues> => {
  return {
    type: SUBMIT_PAYMENT_CORPORATE_PASS,
    payload: values,
  };
};

export const processPayment = (values: CreditCardFormValues): IAction<CreditCardFormValues> => {
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

export const resetPaymentStateOnDestroy = () => {
  return {
    type: RESET_PAYMENT_STATE_ON_DESTROY,
  };
};


export const getCorporatePass = (code: string) => ({
  type: GET_CORPORATE_PASS_REQUEST,
  payload: code,
});

export const applyCorporatePass = pass => ({
  type: APPLY_CORPORATE_PASS,
  payload: pass,
});

export const resetCorporatePass = () => ({
  type: RESET_CORPORATE_PASS,
});

export const changeTab = tab => ({
  type: CHANGE_TAB,
  payload: tab,
});

