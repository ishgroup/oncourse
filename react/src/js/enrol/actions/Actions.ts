import {_toRejectType, _toRequestType} from "../../common/actions/ActionUtils";
import {Phase} from "../reducers/State";
import * as L from "lodash";

//initialize checkout application

export const MessagesSet: string = "checkout/messages/set";
export const MessagesSetRequest: string = _toRequestType(MessagesSet);

export const Init: string = "checkout/init";
export const InitRequest: string = _toRequestType(Init);

//change current phase action
export const PhaseChange: string = "checkout/phase/change";
export const PhaseChangeRequest: string = _toRequestType(PhaseChange);

export const PayerSet: string = "checkout/payer/set";
export const PayerSetRequest: string = _toRequestType(PayerSet);
export const PayerSetReject: string = _toRejectType(PayerSet);

export const showErrors = (error: any, form: string): any => {
  return {
    type: MessagesSetRequest,
    payload: error,
    meta: {
      form: form
    }
  }
};

export const changePhaseRequest = (phase: Phase) => {
  return {
    type: PhaseChangeRequest,
    payload: phase
  }
};

export const changePhase = (phase: Phase) => {

  if (L.isNil(phase))
    throw new Error();

  return {
    type: PhaseChange,
    payload: phase
  }
};
