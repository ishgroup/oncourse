import {CheckoutModelRequest} from "./../../../checkout/CheckoutModelRequest";

export class PaymentRequest {
  checkoutModelRequest?: CheckoutModelRequest;
  merchantReference?: string;

  /**
   * String length 16 of hexadecimal digits
   */
  sessionId?: string;

  /**
   * Amount allocated for credit card payment
   */
  ccAmount?: number;

  /**
   * save CC details to proc
   */
  storeCard?: boolean;
}

