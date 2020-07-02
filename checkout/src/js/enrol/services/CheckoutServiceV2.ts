import {CheckoutV2Api} from "../../http/CheckoutV2Api";
import {DefaultHttpService} from "../../common/services/HttpService";
import {PaymentRequest} from "../../model/v2/checkout/payment/PaymentRequest";
import {PaymentResponse} from "../../model/v2/checkout/payment/PaymentResponse";


class CheckoutServiceV2 {
  readonly checkoutApi = new CheckoutV2Api(new DefaultHttpService());


  public makePayment(paymentRequest: PaymentRequest, xValidateOnly: boolean, payerId: string): Promise<PaymentResponse> {
    return this.checkoutApi.makePayment(paymentRequest,xValidateOnly,payerId);
  }
}

export default new CheckoutServiceV2();
