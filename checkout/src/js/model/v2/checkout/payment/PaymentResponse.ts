import {PaymentStatus} from "./../../../checkout/payment/PaymentStatus";

export class PaymentResponse {
  sessionId?: string;
  merchantReference?: string;
  status?: PaymentStatus;
  reference?: string;
  responseText?: string;
  paymentFormUrl?: string;
}

