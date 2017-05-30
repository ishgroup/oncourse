import { PaymentStatus } from "./../../checkout/payment/PaymentStatus";

export class PaymentResponse {
  sessionId?: string;
  paymentStatus?: PaymentStatus;
  paymentReference?: string;
  applicationIds?: string[];
}

