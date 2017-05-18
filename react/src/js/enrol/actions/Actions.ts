import {_toRejectType, _toRequestType} from "../../common/actions/ActionUtils";

//initialize checkout application

export const MessagesSet: string = "checkout/messages/set";
export const MessagesSetRequest: string = _toRequestType(MessagesSet);

export const Init: string = "checkout/init";
export const InitRequest: string = _toRequestType(Init);

//change current phase action
export const PhaseChange: string = "checkout/phase/change";

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
