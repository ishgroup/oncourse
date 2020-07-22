import {HttpService} from "../common/services/HttpService";
import {PaymentRequest,PaymentResponse} from "../model";

export class CheckoutV2Api {
  constructor(private http: HttpService) {
  }

  getStatus(sessionId: string, payerId: string): Promise<PaymentResponse> {
    return this.http.GET(`/v2/getPaymentStatus/${sessionId}`, {headers: {payerId}});
  }
  makePayment(paymentRequest: PaymentRequest, xValidateOnly: boolean, payerId: string): Promise<PaymentResponse> {
    return this.http.POST(
      `/v2/makePayment`,
      paymentRequest,
      {headers: {xValidateOnly, payerId, xOrigin: window.location.href}},
    );
  }
}
