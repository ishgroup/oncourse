import {PaymentStatus} from "./PaymentStatus";

export class PaymentResponse {
  sessionId?: string;
  status?: PaymentStatus;
  reference?: string;
}

