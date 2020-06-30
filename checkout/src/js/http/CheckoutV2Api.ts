import {HttpService} from "../common/services/HttpService";
import {PaymentRequest} from "../model/v2/checkout/payment/PaymentRequest";
import {PaymentResponse} from "../model/v2/checkout/payment/PaymentResponse";

export class CheckoutV2Api {
  constructor(private http: HttpService) {
  }

  makePayment(paymentRequest: PaymentRequest, xValidateOnly: boolean, payerId: string, referer: string): Promise<PaymentResponse> {
    return this.http.POST(
      `/v2/makePayment`,
      paymentRequest,
      {headers: {xValidateOnly, payerId}},
    );
  }
}
