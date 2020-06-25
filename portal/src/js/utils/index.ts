import {PaymentRequest} from "../model/api";

export const getPaymentRequest = (ccAmount: number, details?: PaymentRequest): PaymentRequest => ({
  ccAmount,
  merchantReference: details ? details.merchantReference : null,
  sessionId: null,
  storeCard: false,
  checkoutModelRequest: null
});
