import { PaymentStatus } from "./../../checkout/payment/PaymentStatus";

export class PaymentResponse {
  sessionId?: string;
  paymentStatus?: PaymentStatus;
  error?: string;
  paymentReference?: string;
  applicationIds?: string[];
}

