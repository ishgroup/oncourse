import {_toRequestType} from "../../common/actions/ActionUtils";
import {Phase} from "../reducers/State";
import * as L from "lodash";
import {AxiosResponse} from "axios";
import {Amount} from "../../model/checkout/Amount";
import {IAction} from "../../actions/IshAction";

// initialize checkout application

export const SHOW_MESSAGES: string = "checkout/messages/show";
export const SHOW_MESSAGES_REQUEST: string = _toRequestType(SHOW_MESSAGES);

export const INIT_REQUEST: string = "checkout/init/request";

// change current phase action
export const CHANGE_PHASE: string = "checkout/phase/change";

export const SET_PAYER_TO_STATE: string = "checkout/set/payer/to/state";

export const GET_AMOUNT: string = "checkout/get/amount";
export const UPDATE_AMOUNT: string = "checkout/update/amount";

export const SET_NEW_CONTACT_FLAG = "checkout/set/new/contact/flag";

export const RESET_CHECKOUT_STATE = "checkout/reset/state";

export const FINISH_CHECKOUT_PROCESS = "checkout/finish/process";

export const ADD_CODE: string = "checkout/summary/add/code";
export const ADD_CODE_REQUEST: string = _toRequestType(ADD_CODE);

export const GET_CHECKOUT_MODEL_FROM_BACKEND: string = "checkout/get/model/from/backend";

export const ADD_REDEEM_VOUCHER_TO_STATE: string = "checkout/add/redeemVoucher";

export const TOGGLE_VOUCHER_ACTIVITY: string = "checkout/set/voucher/activity";

export const addCode = (code: string): { type: string, payload: string } => {
  return {
    type: ADD_CODE_REQUEST,
    payload: code,
  };
};

export const getCheckoutModelFromBackend = (): IAction<any> => {
  return {
    type: GET_CHECKOUT_MODEL_FROM_BACKEND,
  };
};

export const sendInitRequest = (): IAction<any> => {
  return {
    type: INIT_REQUEST,
  };
};

export const setNewContactFlag = (newContact: boolean): { type: string, payload: boolean } => {
  return {
    type: SET_NEW_CONTACT_FLAG,
    payload: newContact,
  };
};

export const showFormValidation = (response: AxiosResponse, form: string): any => {
  return {
    type: SHOW_MESSAGES_REQUEST,
    payload: response,
    meta: {
      form,
    },
  };
};

export const changePhase = (phase: Phase) => {

  if (L.isNil(phase))
    throw new Error();

  return {
    type: CHANGE_PHASE,
    payload: phase,
  };
};

export const getAmount = (): { type: string } => {
  return {
    type: GET_AMOUNT,
  };
};

export const updateAmount = (amount: Amount): IAction<Amount> => {
  return {
    type: UPDATE_AMOUNT,
    payload: amount,
  };
};

export const toggleVoucher = (id: string, enabled: boolean): IAction<any> => {
  return {
    type: TOGGLE_VOUCHER_ACTIVITY,
    payload: {id, enabled},
  };
}

export const finishCheckoutProcess = (): IAction<any> => {
  return {
    type: FINISH_CHECKOUT_PROCESS,
  };
};

export const resetCheckoutState = (): IAction<any> => {
  return {
    type: RESET_CHECKOUT_STATE,
  };
};

export const setPayer = function (id: string): { type: string, payload: string } {
  return {type: SET_PAYER_TO_STATE, payload: id};
};
