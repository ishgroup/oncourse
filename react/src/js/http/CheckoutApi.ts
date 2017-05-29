import {HttpService} from "../common/services/HttpService";
import { CheckoutModel } from "../model/checkout/CheckoutModel";
import { CheckoutModelRequest } from "../model/checkout/CheckoutModelRequest";
import { ContactNode } from "../model/checkout/ContactNode";
import { PaymentRequest } from "../model/checkout/payment/PaymentRequest";
import { PaymentResponse } from "../model/checkout/payment/PaymentResponse";
import { PurchaseItemsRequest } from "../model/checkout/request/PurchaseItemsRequest";
import { CommonError } from "../model/common/CommonError";

export class CheckoutApi {
  constructor(private http: HttpService) {
  }

  getCheckoutModel(checkoutModelRequest: CheckoutModelRequest): Promise<CheckoutModel> {
    return this.http.POST(`/getCheckoutModel`, checkoutModelRequest)
  }
  getPurchaseItems(purchaseItemsRequest: PurchaseItemsRequest): Promise<ContactNode> {
    return this.http.POST(`/purchaseItems`, purchaseItemsRequest)
  }
  makePayment(paymentRequest: PaymentRequest): Promise<PaymentResponse> {
    return this.http.POST(`/makePayment`, paymentRequest)
  }
}
