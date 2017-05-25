import {HttpService} from "../common/services/HttpService";
import { CheckoutModel } from "../model/checkout/CheckoutModel";
import { PurchaseItems } from "../model/checkout/PurchaseItems";
import { PaymentRequest } from "../model/checkout/payment/PaymentRequest";
import { PurchaseItemsRequest } from "../model/checkout/request/PurchaseItemsRequest";
import { CommonError } from "../model/common/CommonError";

export class CheckoutApi {
  constructor(private http: HttpService) {
  }

  calculateAmount(checkoutModel: CheckoutModel): Promise<CheckoutModel> {
    return this.http.POST(`/calculateAmount`, checkoutModel)
  }
  getPurchaseItems(purchaseItemsRequest: PurchaseItemsRequest): Promise<PurchaseItems> {
    return this.http.POST(`/purchaseItems`, purchaseItemsRequest)
  }
  makePayment(paymentRequest: PaymentRequest): Promise<CheckoutModel> {
    return this.http.POST(`/makePayment`, paymentRequest)
  }
}
