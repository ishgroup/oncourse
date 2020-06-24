import {CheckoutModelRequest, PaymentRequest} from "../model/api";

export const getPaymentRequest = (ccAmount: number, payerId: string): PaymentRequest => ({
  ccAmount,
  merchantReference: "",
  sessionId: "",
  storeCard: false,
  checkoutModelRequest: {
    payerId,
    contactNodes: [],
    promotionIds: [],
    redeemedVoucherIds: [],
    applyCredit: false,
    payNow: ccAmount,
    corporatePassId: ""
  }
})
