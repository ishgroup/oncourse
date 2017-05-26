import {CheckoutModel} from "./../../checkout/CheckoutModel";

export class PaymentRequest {
  checkoutModel?: CheckoutModel;
  creditCardNumber?: string;
  creditCardName?: string;
  expiryMonth?: string;
  expiryYear?: string;
  creditCardCvv?: string;
  agreementFlag?: boolean;
}

