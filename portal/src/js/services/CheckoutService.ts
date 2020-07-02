import {CheckoutApi, PaymentResponse, PaymentRequest } from "../model/api";
import {DefaultHttpService} from "./HttpService";

class CheckoutService {
  readonly checkoutApi = new CheckoutApi(new DefaultHttpService());


  public makePayment(paymentRequest: PaymentRequest, xValidateOnly: boolean, payerId: string): Promise<PaymentResponse> {
    return this.checkoutApi.makePayment(paymentRequest, xValidateOnly, payerId);
  }

  public getPaymentStatus(sessionId:string, payerId: string): Promise<PaymentResponse> {
    return this.checkoutApi.getPaymentStatus(sessionId, payerId);
  }
}

export default new CheckoutService();
