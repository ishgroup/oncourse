import {HttpService} from "../common/services/HttpService";
import {
  CheckoutModel, CheckoutModelRequest, ContactNode, PaymentRequest, PaymentResponse, ContactNodeRequest, Preferences
} from "../model";

export class CheckoutApi {
  constructor(private http: HttpService) {
  }

  getCheckoutModel(checkoutModelRequest: CheckoutModelRequest): Promise<CheckoutModel> {
    return this.http.POST(`/getCheckoutModel`, checkoutModelRequest);
  }

  getContactNode(contactNodeRequest: ContactNodeRequest): Promise<ContactNode> {
    return this.http.POST(`/getContactNode`, contactNodeRequest);
  }

  getPaymentStatus(sessionId: string): Promise<PaymentResponse> {
    return this.http.GET(`/getPaymentStatus/${sessionId}`);
  }

  makePayment(paymentRequest: PaymentRequest): Promise<PaymentResponse> {
    return this.http.POST(`/makePayment`, paymentRequest);
  }
}
