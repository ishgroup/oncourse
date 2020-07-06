import {HttpService} from "../common/services/HttpService";
import {
  CheckoutModel,
  CheckoutModelRequest,
  ContactNode,
  PaymentRequest,
  PaymentResponse,
  ContactNodeRequest,
} from "../model";

export class CheckoutApi {
  constructor(private http: HttpService) {
  }

  getCheckoutModel(checkoutModelRequest: CheckoutModelRequest): Promise<CheckoutModel> {
    return this.http.POST(`/v1/getCheckoutModel`, checkoutModelRequest);
  }
  getContactNode(contactNodeRequest: ContactNodeRequest): Promise<ContactNode> {
    return this.http.POST(`/v1/getContactNode`, contactNodeRequest);
  }
  getPaymentStatus(sessionId: string): Promise<PaymentResponse> {
    return this.http.GET(`/v1/getPaymentStatus/${sessionId}`);
  }
  makePayment(paymentRequest: PaymentRequest): Promise<PaymentResponse> {
    return this.http.POST(`/v1/makePayment`, paymentRequest);
  }
}
