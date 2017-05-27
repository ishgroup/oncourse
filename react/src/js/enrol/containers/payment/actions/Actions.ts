import {_toRequestType} from "../../../../common/actions/ActionUtils";

export const OpenPayment: string = "checkout/payment/open";
export const OpenPaymentRequest: string = _toRequestType(OpenPayment);


export const openPayment = (): { type: string } => {
  return {
    type: OpenPaymentRequest
  }
};
