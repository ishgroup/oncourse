import {HttpService} from "../common/services/HttpService";
import {
  CheckoutModel,
  CheckoutModelRequest,
  PaymentResponse,
} from "../model";

export class CheckoutApi {
  constructor(private http: HttpService) {
  }
  getCheckoutModel(checkoutModelRequest: CheckoutModelRequest): Promise<CheckoutModel> {
    return this.http.POST(`/v1/getCheckoutModel`, checkoutModelRequest);
  }
  getPaymentStatus(sessionId: string): Promise<PaymentResponse> {
    return this.http.GET(`/v1/getPaymentStatus/${sessionId}`);
  }
}
