export interface ContactNode {
  contactId: string;
  enrolments: any[];
  applications: any[];
  articles: any[];
  memberships: any[];
  vouchers: any[];
  waitingLists: any[];
}

export interface CheckoutModelRequest {
  contactNodes: ContactNode[];
  promotionIds: string[];
  redeemedVoucherIds: string[];
  payerId: string;
  applyCredit: boolean;
  payNow: number;
  corporatePassId: string;
}

export interface PaymentRequest {
  checkoutModelRequest?: any;
  merchantReference: any;
  sessionId: any;
  ccAmount: number;
  storeCard: boolean;
}

export type PaymentStatus = "IN_PROGRESS" | "FAILED" | "SUCCESSFUL" |"SUCCESSFUL_WAITING_COURSES" | "SUCCESSFUL_BY_PASS"

export interface PaymentResponse {
  sessionId: string;
  merchantReference: string;
  paymentFormUrl: string;
  status: PaymentStatus;
  reference: string;
  responseText: string;
}


export class CheckoutApi {
  constructor(private http: any) {
  }


  makePayment(paymentRequest: PaymentRequest, xValidateOnly: boolean, payerId: string): Promise<PaymentResponse> {
    return this.http.POST(`/v2/makePayment`, paymentRequest, { headers: { xValidateOnly, payerId }, params: { }, responseType: '' });
  }
}
