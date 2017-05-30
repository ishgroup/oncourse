import {_toRejectType, _toRequestType} from "../../common/actions/ActionUtils";
import {Phase} from "../reducers/State";
import * as L from "lodash";
import {AxiosResponse} from "axios";
import {Amount} from "../../model/checkout/Amount";

//initialize checkout application

export const SHOW_MESSAGES: string = "checkout/messages/show";
export const SHOW_MESSAGES_REQUEST: string = _toRequestType(SHOW_MESSAGES);

export const Init: string = "checkout/init";
export const InitRequest: string = _toRequestType(Init);

//change current phase action
export const CHANGE_PHASE: string = "checkout/phase/change";
export const CHANGE_PHASE_REQUEST: string = _toRequestType(CHANGE_PHASE);

export const PayerSet: string = "checkout/payer/set";
export const PayerSetRequest: string = _toRequestType(PayerSet);
export const PayerSetReject: string = _toRejectType(PayerSet);

export const UPDATE_AMOUNT: string = "checkout/amount/update";
export const UPDATE_AMOUNT_REQUEST: string = _toRequestType(UPDATE_AMOUNT);

export const showFormValidation = (response: AxiosResponse, form: string): any => {
  return {
    type: SHOW_MESSAGES_REQUEST,
    payload: response,
    meta: {
      form: form
    }
  }
};

export const changePhaseRequest = (phase: Phase) => {
  return {
    type: CHANGE_PHASE_REQUEST,
    payload: phase
  }
};

export const changePhase = (phase: Phase) => {

  if (L.isNil(phase))
    throw new Error();

  return {
    type: CHANGE_PHASE,
    payload: phase
  }
};

export const updateAmountRequest = (): { type: string } => {
  return {
    type: UPDATE_AMOUNT_REQUEST
  }
};

export const updateAmount = (amount: Amount): { type: string, payload: Amount } => {
  return {
    type: UPDATE_AMOUNT,
    payload: amount
  }
};
