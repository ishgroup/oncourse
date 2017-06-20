import {CheckoutModelRequest} from "../model/../../checkout/CheckoutModelRequest";

export class PaymentRequest {
  checkoutModelRequest?: CheckoutModelRequest;
  creditCardNumber?: string;
  creditCardName?: string;
  expiryMonth?: string;
  expiryYear?: string;
  creditCardCvv?: string;
  agreementFlag?: boolean;

  /**
   * String length 16 of hexadecimal digits
   */
  sessionId?: string;
  payNow?: string;
}

