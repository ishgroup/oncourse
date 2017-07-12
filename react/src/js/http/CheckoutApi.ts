import {HttpService} from "../common/services/HttpService";
import {CheckoutModel} from "../model/checkout/CheckoutModel";
import {CheckoutModelRequest} from "../model/checkout/CheckoutModelRequest";
import {ContactNode} from "../model/checkout/ContactNode";
import {PaymentRequest} from "../model/checkout/payment/PaymentRequest";
import {PaymentResponse} from "../model/checkout/payment/PaymentResponse";
import {ContactNodeRequest} from "../model/checkout/request/ContactNodeRequest";

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
  updateParentChilds(parentId:string, childIds: string[]): Promise<any> {
    return this.http.POST('/updateParentChilds', {parentId, childIds});
  }
  getCorporatePass(code: string): Promise<any> {
    return this.http.GET('/getCorporatePass', code);
  }
}
