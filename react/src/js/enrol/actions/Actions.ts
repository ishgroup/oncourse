import {_toRejectType, _toRequestType} from "../../common/actions/ActionUtils";
import {Phase} from "../reducers/State";
import * as L from "lodash";
import {AxiosResponse} from "axios";
import {Amount} from "../../model/checkout/Amount";
import {IAction} from "../../actions/IshAction";

//initialize checkout application

export const SHOW_MESSAGES: string = "checkout/messages/show";
export const SHOW_MESSAGES_REQUEST: string = _toRequestType(SHOW_MESSAGES);

export const INIT_REQUEST: string = "checkout/init/request";

//change current phase action
export const CHANGE_PHASE: string = "checkout/phase/change";
export const CHANGE_PHASE_REQUEST: string = _toRequestType(CHANGE_PHASE);

export const PayerSet: string = "checkout/payer/set";
export const PayerSetRequest: string = _toRequestType(PayerSet);
export const PayerSetReject: string = _toRejectType(PayerSet);

export const UPDATE_AMOUNT: string = "checkout/amount/update";
export const UPDATE_AMOUNT_REQUEST: string = _toRequestType(UPDATE_AMOUNT);

export const SET_NEW_CONTACT_FLAG = "checkout/set/new/contact/flag";

export const RESET_CHECKOUT_STATE = "checkout/reset/state";

export const FINISH_CHECKOUT_PROCESS = "checkout/finish/process";

export const ADD_CODE: string = "checkout/summary/add/code";
export const ADD_CODE_REQUEST: string = _toRequestType(ADD_CODE);

export const UPDATE_SUMMARY : string = "checkout/summary/update";
export const UPDATE_SUMMARY_REQUEST: string = _toRequestType(UPDATE_SUMMARY);

export const addCode = (code: string): { type: string, payload: string } => {
  return {
    type: ADD_CODE_REQUEST,
    payload: code,
  };
};

export const updateSummaryRequest = (): { type: string } => {
  return {
    type: UPDATE_SUMMARY_REQUEST,
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

export const changePhaseRequest = (phase: Phase) => {
  return {
    type: CHANGE_PHASE_REQUEST,
    payload: phase,
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

export const updateAmountRequest = (): { type: string } => {
  return {
    type: UPDATE_AMOUNT_REQUEST,
  };
};

export const updateAmount = (amount: Amount): IAction<Amount> => {
  return {
    type: UPDATE_AMOUNT,
    payload: amount,
  };
};

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
