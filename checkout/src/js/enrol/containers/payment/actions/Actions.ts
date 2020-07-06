import {IAction} from "../../../../actions/IshAction";
import {CreditCardFormValues, CorporatePassFormValues} from "../services/PaymentService";
import {GABuilder} from "../../../../services/GoogleAnalyticsService";
import {Phase} from "../../../reducers/State";
import {PaymentResponse} from "../../../../model";

export const SUBMIT_PAYMENT_CREDIT_CARD: string = "checkout/payment/submit/creditCard";
export const SUBMIT_PAYMENT_CORPORATE_PASS: string = "checkout/payment/submit/corporatePass";
export const SUBMIT_PAYMENT_FOR_WAITING_COURSES: string = "checkout/payment/submit/waitingCourses";
export const PROCESS_PAYMENT: string = "checkout/payment/process/payment";
export const PROCESS_PAYMENT_V2: string = "checkout/v2/payment/process/payment";
export const PROCESS_PAYMENT_V2_FAILED_STATUS: string = "checkout/v2/payment/process/paymentStatus";

export const SET_PAYMENT_DATA = "checkout/payment/set/paymentData";

export const SET_IFRAME_URL = "checkout/payment/set/iframeUrl";

export const GENERATE_WAITING_COURSES_RESULT_DATA = "checkout/payment/result/waitingCourses/data";

export const UPDATE_PAYMENT_STATUS = "checkout/payment/update/payment/status";

export const GET_PAYMENT_STATUS = "checkout/payment/get/payment/status";

export const RESET_PAYMENT_STATE = "checkout/payment/reset/payment/state";
export const RESET_PAYMENT_STATE_ON_INIT: string = "checkout/payment/reset/payment/onInit";

export const GET_CORPORATE_PASS_REQUEST = "checkout/payment/get/corporatePass";
export const APPLY_CORPORATE_PASS = "checkout/payment/apply/corporatePass";
export const RESET_CORPORATE_PASS = "checkout/payment/reset/corporatePass";

export const CHANGE_TAB = "checkout/payment/change/tab";

export const PROCESSING_MANDATORY_FIELDS = "checkout/payment/processing/mandatoryFields";

export const submitPaymentCreditCard = (values: CreditCardFormValues): IAction<CreditCardFormValues> => {
  return {
    type: SUBMIT_PAYMENT_CREDIT_CARD,
    payload: values,
  };
};

export const submitPaymentForWaitingCourses = (values: CreditCardFormValues): IAction<CreditCardFormValues> => {
  return {
    type: SUBMIT_PAYMENT_FOR_WAITING_COURSES,
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

export const processPaymentV2 =
  (xValidateOnly: boolean, payerId: string) => {
    return {
      type: PROCESS_PAYMENT_V2,
      payload: {xValidateOnly, payerId},
    };
  };

export const processPaymentV2FailedStatus = () => ({
  type: PROCESS_PAYMENT_V2_FAILED_STATUS,
});

export const setIframeUrl = (url: string) => ({
  type: SET_IFRAME_URL,
  payload: url,
});

export const getPaymentStatus = (): IAction<any> => {
  return {
    type: GET_PAYMENT_STATUS,
  };
};

export const updatePaymentStatus = (response: PaymentResponse): IAction<PaymentResponse> => {
  return {
    type: UPDATE_PAYMENT_STATUS,
    payload: response,
    meta: {
      analytics: GABuilder.purchaseItems(response),
    },
  };
};

export const resetPaymentState = () => {
  return {
    type: RESET_PAYMENT_STATE,
  };
};

export const resetPaymentStateOnInit = () => {
  return {
    type: RESET_PAYMENT_STATE_ON_INIT,
  };
};

export const generateWaitingCoursesResultData = data => {
  return {
    type: GENERATE_WAITING_COURSES_RESULT_DATA,
    payload: data,
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


export const setPaymentData = (data: PaymentResponse) => ({
  type: SET_PAYMENT_DATA,
  payload: data,
});

export const changeTab = tab => ({
  type: CHANGE_TAB,
  payload: tab,
  meta: {
    analytics: GABuilder.setCheckoutStep(Phase.Payment, tab),
  },
});

export const processingMandatoryFields = () => ({
  type: PROCESSING_MANDATORY_FIELDS,
});

