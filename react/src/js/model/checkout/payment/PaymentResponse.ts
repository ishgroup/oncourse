import {PaymentStatus} from "./../../checkout/payment/PaymentStatus";

export class PaymentResponse {
  sessionId?: string;
  status?: PaymentStatus;
  reference?: string;
}

