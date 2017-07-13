import {CheckoutModelRequest} from "../CheckoutModelRequest";

export class CorporatePassPaymentRequest {
  checkoutModelRequest?: CheckoutModelRequest;
  agreementFlag?: boolean;
  corporatePassId?: string;
  reference?: string;

  /**
   * String length 16 of hexadecimal digits
   */
  sessionId?: string;
}

