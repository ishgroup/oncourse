import {CheckoutV2Api} from "../../http/CheckoutV2Api";
import {DefaultHttpService} from "../../common/services/HttpService";
import {PaymentRequest,PaymentResponse} from "../../model";

class CheckoutServiceV2 {
  readonly checkoutApi = new CheckoutV2Api(new DefaultHttpService());


  public makePayment(paymentRequest: PaymentRequest, xValidateOnly: boolean, payerId: string): Promise<PaymentResponse> {
    return this.checkoutApi.makePayment(paymentRequest,xValidateOnly,payerId);
  }
  public getStatus(sessionId: string, payerId: string): Promise<PaymentResponse> {
    return this.checkoutApi.getStatus(sessionId,payerId);
  }
}

export default new CheckoutServiceV2();
