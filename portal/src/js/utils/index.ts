import {PaymentRequest,PaymentResponse} from "../model/api";

export const getPaymentRequest = (ccAmount: number, details?: PaymentResponse): PaymentRequest => {
  const {merchantReference, sessionId} = details || {};

  return  {
    ccAmount,
    merchantReference,
    sessionId,
    storeCard: false,
    checkoutModelRequest: null,
  };
};
