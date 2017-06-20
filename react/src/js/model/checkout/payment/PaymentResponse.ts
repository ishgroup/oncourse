import {PaymentStatus} from "../model/../../checkout/payment/PaymentStatus";

export class PaymentResponse {
  sessionId?: string;
  status?: PaymentStatus;
  reference?: string;
}

